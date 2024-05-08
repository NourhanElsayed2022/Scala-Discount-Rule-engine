CREATE TABLE Orders_Discount (
    Order_Date VARCHAR2(50),
    Product_Name VARCHAR2(100),
    Expiry_Date VARCHAR2(25),
    Quantity NUMBER,
    Unit_Price NUMBER(10, 2),
    Channel VARCHAR2(50),
    Payment_Method VARCHAR2(50),
    Order_Before_Discount NUMBER(10, 2),
    Discount VARCHAR2(10),
    Order_After_Discount NUMBER(10, 2)
);




