package item.com.sokcet;

public class TextBean {
    private String price;
    private String amount;

    public TextBean(String price, String amount) {
        this.price = price;
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TextBean{" +
                "price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof TextBean) {
            TextBean textBean = (TextBean) o;
            return this.price.equals(textBean.price);
        }
        return super.equals(o);
    }


}
