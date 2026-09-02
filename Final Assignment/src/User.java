public abstract class User {
    private final int id; private final String name; private final String username;
    protected User(int id,String name,String username){this.id=id;this.name=name;this.username=username;}
    public int getId(){return id;} public String getName(){return name;} public String getUsername(){return username;}
    public abstract void openDashboard();
}
class Customer extends User { public Customer(int id,String name,String username){super(id,name,username);} public void openDashboard(){new CustomerFrame(this);} }
class Admin extends User { public Admin(int id,String name,String username){super(id,name,username);} public void openDashboard(){new AdminFrame(this);} }
