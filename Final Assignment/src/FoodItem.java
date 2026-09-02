public class FoodItem {
    private final int id; private final String name; private final String category; private final double price; private int stock;
    public FoodItem(int id,String name,String category,double price,int stock){this.id=id;this.name=name;this.category=category;this.price=price;this.stock=stock;}
    public int getId(){return id;} public String getName(){return name;} public String getCategory(){return category;} public double getPrice(){return price;} public int getStock(){return stock;} public void setStock(int stock){this.stock=stock;}
}
class CartItem { private final FoodItem food; private int quantity; public CartItem(FoodItem food,int quantity){this.food=food;this.quantity=quantity;} public FoodItem getFood(){return food;} public int getQuantity(){return quantity;} public void setQuantity(int q){quantity=q;} public double getTotal(){return food.getPrice()*quantity;} }
class InsufficientStockException extends Exception { public InsufficientStockException(String msg){super(msg);} }
