import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class RubricaProva {
	static Scanner in;
	public static void main(String[] args) {
				// create connection
				Db db1 = new Db("phonebook", "contacts", "root", "root");
				db1.connect();
				System.out.println("Hello! Welcome to the PhoneBook\n");
				String[] values = {"name", "lastname", "number", "favourites", "email"};
				// db1.view(values);
				// Scan input
				in = new Scanner(System.in);
				int c1, c2;
				boolean f1 = false, f2 = false;
				
				while (f1 == false) {
					System.out.println(
						"Please select an option: \n"
						+ "[1] Show contacts [1]\n"
						+ "[2] Show favourites [2]\n"
						+ "[3] Search [3]\n"
						+ "[0] Exit [0]"
					);
					c1 = Integer.parseInt(in.nextLine());

					switch( c1 ) {
						case 1:
							db1.view(values);
							f2 = false;
							System.out.println(
									"\nPlease select an option: \n"
									+ "[1] Add contact\n"
									+ "[2] Remove contact\n"
									+ "[3] Edit contact\n"
									+ "[0] Return"
								);
								c2 = Integer.parseInt(in.nextLine());
								
								while (f2 == false) {
									switch( c2 ) {
										case 1:
											db1.add(values);
											f2 = true;
											break;
										case 2:
											db1.delete();
											f2 = true;
											break;
										case 3:
											db1.edit(values);
											f2 = true;
											break;
										case 0:
											f2 = true;
											break;
										default:
											System.out.println("Option not available");
									}
								}
							break;
						case 2:
							db1.viewFav(values);
							break;
						case 3:
							db1.search(values);
							f2 = false;
							System.out.println(
									"\nPlease select an option: \n"
									+ "[1] Remove contact\n"
									+ "[2] Edit contact\n"
									+ "[0] Return"
								);
								c2 = Integer.parseInt(in.nextLine());
								
								while (f2 == false) {
									switch( c2 ) {
										case 1:
											db1.delete();
											f2 = true;
											break;
										case 2:
											db1.edit(values);
											f2 = true;
											break;
										case 0:
											f2 = true;
											break;
										default:
											System.out.println("Option not available");
									}
								}
							break;
						case 0:
							f1 = true;
							break;
						default:
							System.out.println("Wrong choice");
						}
				}
				db1.close();
				in.close();
		}
}

class Db {
	final String dbName;
	final String user;
	final String password;
	
	String table;
	String driver;
	String url;
	Connection con;
	Scanner in;
	
	Db(String dbn, String t, String un, String pw) {
		dbName = dbn;
		table = t;
		user = un;
		password = pw;
		connect();
	}
	
	void connect() {
		try {
			driver = "com.mysql.cj.jdbc.Driver";
			Class.forName(driver);
			url = 
	"jdbc:mysql://localhost:3307/"+dbName+"?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
			con = DriverManager.getConnection(url, user, password);	
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void view(String[] fields) {
		String query = "SELECT * FROM " + table + " ORDER BY " + fields[0];
		
		try {
			Statement cmd = con.createStatement();
			ResultSet res = cmd.executeQuery(query);
			
			while (res.next()) {
				System.out.print("(ID: " + res.getString("id") + ") ");
				for(int i = 0; i < fields.length; i++) {
					System.out.print(res.getString(fields[i]) + ( (i == fields.length-1) ? "" : "\t\t|\t" ));
				}
				System.out.println();
			}
			res.close();
			cmd.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
	}
	
	void viewFav(String[] fields) {
		String query = "SELECT * FROM " + table + " WHERE favourites = '1'";
		
		try {
			Statement cmd = con.createStatement();
			ResultSet res = cmd.executeQuery(query);
			while (res.next()) {
				for(int i = 0; i < fields.length; i++) {
					System.out.print(res.getString(fields[i]) + " ");
				}
				System.out.println();
			}
			
			res.close();
			cmd.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
	}
	void search(String[] fields) {
		in = new Scanner(System.in);
		System.out.println("Search..");
		String like = in.nextLine();
		String query = "SELECT ";
		
		for(int i = 0; i < fields.length; i++) {
			query += fields[i] + ( (i == fields.length-1) ? "" : ", " );
		}
		
		query += " FROM " + table
				+ " WHERE ";
		for(int i=0; i < fields.length; i++) {
			query += fields[i] + " LIKE '%" +like+ "%'" + ( (i == fields.length-1) ? "" : "OR " );
		}
		
		try {
			Statement cmd = con.createStatement();
			ResultSet res = cmd.executeQuery(query);
			while (res.next()) {
				for(int i = 0; i < fields.length; i++) {
					System.out.print(res.getString(fields[i]) + " ");
				}
				System.out.println();
			}
			res.close();
			cmd.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void add(String[] fields) {
		in = new Scanner(System.in);
		String n = "", ln = "", num = "",  email = "",  fav = "0";
		
		System.out.println("Insert name: ");
		n = in.nextLine();
		System.out.println("Insert lastname: ");
		ln = in.nextLine();
		System.out.println("Insert number: ");
		num = in.nextLine();
		System.out.println("Insert email: ");
		email = in.nextLine();
		
		String[] values = {n, ln, num, email, fav};
		
		String query = "INSERT INTO "+table+" (";
		for(int i = 0; i < fields.length; i++) {
			query += fields[i] + ( (i == fields.length-1) ? "" : ", " );
		}
		query += ") VALUES(";
		for(int i = 0; i < values.length; i++) {
			query += "'" + values[i] + "'" + ( (i == fields.length-1) ? "" : ", " );
		}
		query += ")";
		
		try {
			Statement cmd = con.createStatement();
			cmd.executeUpdate(query);
			System.out.println("Insert done!");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void delete() {
		in = new Scanner(System.in);
		
		System.out.println("Select the ID to remove the contact: ");
		int id = Integer.parseInt(in.nextLine());
		
		String query = "DELETE FROM "+table+" WHERE id = " + id;
		
		try {
			Statement cmd = con.createStatement();
			cmd.executeUpdate(query);
			System.out.println("Delete done!");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void edit(String[] fields) {
		in = new Scanner (System.in);
		String field = "", value = "";
		boolean z = false;

		System.out.println("Who would you like to edit: ");
		int id = Integer.parseInt(in.nextLine());
		System.out.println("What would you like to edit: \n"
				+ "[1] Name\n"
				+ "[2] Lastname\n"
				+ "[3] Number\n"
				+ "[4] Email\n"
				+ "[5] Add or remove from favourites\n");
		while (z == false) {
		int editNumber = Integer.parseInt(in.nextLine());
			switch (editNumber) {
				case 1:
					field = "Name";
					z = true;
					break;
				case 2:
					field = "LastName";
					z = true;
					break;
				case 3:
					field = "Number";
					z = true;
					break;
				case 4:
					field = "Email";
					z = true;
					break;
				case 5:
					field = "Favourites";
					z = true;
					break;
				default:
					System.out.println("Field not available");
			}
		}
		String query = "";
		if (field.equals("Favourites")) {
			query = "SELECT * FROM " + table;
			try {
				Statement cmd = con.createStatement();
				ResultSet res = cmd.executeQuery(query);
				while (res.next()) {
						if (res.getString(fields[4]).equals("0"))
							value = "1";
						else value = "0";
					}
					System.out.println();
				}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("What value?");
			value = in.nextLine();
		}
		query = String.format("UPDATE %s SET %s = '%s' WHERE id = %s"
				, table, field, value, id);
		try {
			Statement cmd = con.createStatement();
			cmd.executeUpdate(query);
			System.out.println("Update done!");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void close() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
