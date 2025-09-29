package controller.stock_controller;

import lombok.Setter;
import model.stock_service.StockDAO_User;
import vo.Items.Item;
import vo.Stocks.Stock;

import java.util.List;

@Setter
public class Stock_User_Controller implements  Stock_User_Controller_Interface{

    // view에서 받은 거를가지고 DAO호출
    StockDAO_User sUser = new StockDAO_User();


    @Override
    public List<Stock> stockCurrentSearch(int uID) {
        return sUser.stockCurrentSearch(uID);
    }

    @Override
    public Stock stockPrint(int stockID, int userID) {
        return sUser.stockPrint(stockID, userID);
    }

    @Override
    public int stockRequest(Item item, Stock stock) {
        return sUser.stockRequest(item, stock);
    }

    @Override
    public int deleteStock(int stockID) {
        return sUser.deleteStock(stockID);
    }

}
