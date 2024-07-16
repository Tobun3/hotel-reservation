package models;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Bill {
    private IntegerProperty billId;
    private DoubleProperty amountToPay;
    private BooleanProperty isSettle;
    private DoubleProperty discount;
    private ObjectProperty<LocalDate> payDate;

    public Bill() {
        this.billId = new SimpleIntegerProperty(this, "billId", 0);
        this.amountToPay = new SimpleDoubleProperty(this, "amountToPay", 0.0);
        this.isSettle = new SimpleBooleanProperty(this, "isSettle", false);
        this.discount = new SimpleDoubleProperty(this, "discount", 0.0);
        this.payDate = new SimpleObjectProperty<>(LocalDate.now());
    }

    // Getter and Setters
    // BillId
    public IntegerProperty getBillIdProperty() {
        return billId;
    }
    public int getBillId() {
        return billId.get();
    }
    public void setBillId(int billId) {
        this.billId.set(billId);
    }

    // AmountToPay
    public DoubleProperty getAmountToPayProperty() {
        return amountToPay;
    }
    public double getAmountToPay() {
        return amountToPay.get();
    }
    public void setAmountToPay(double amountToPay) {
        this.amountToPay.set(amountToPay);
    }

    // IsSettle
    public BooleanProperty getIsSettleProperty() {
        return isSettle;
    }
    public boolean getIsSettle() {
        return isSettle.get();
    }
    public void setIsSettle(boolean isSettle) {
        this.isSettle.set(isSettle);
    }

    // Discount
    public DoubleProperty getDiscountProperty() {
        return discount;
    }
    public double getDiscount() {
        return discount.get();
    }
    public void setDiscount(double discount) {
        this.discount.set(discount);
    }
    
    // pay date
    public ObjectProperty<LocalDate> payDateProperty() {
        return this.payDate;
    }
    public LocalDate getPayDate() {
        return this.payDateProperty().get();
    }
    public void setPayDate(final LocalDate payDate) {
        this.payDateProperty().set(payDate);
    }
    
}