package package_1;

public class Menu {
	private String name;
	private int price;
	private int quantity;
	private static int DFQ = 30;
	
	public Menu(String n, int p, int q) {
		this.setName(n);
		this.setPrice(p);
		this.setQuantity(q);
	}
	public Menu(String n, int p) {
		this(n,p,DFQ);
	}
	public Menu() {
		this("", 0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public void setDFQ(int d) {
		Menu.DFQ = d;
	}
	public String toString() {
		return name+"\t"+Integer.toString(price)+"\t"+Integer.toString(quantity);
	}

}
