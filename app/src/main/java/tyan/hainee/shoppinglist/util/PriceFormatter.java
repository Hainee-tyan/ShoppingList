package tyan.hainee.shoppinglist.util;

public class PriceFormatter {
    private final String TAG = "PriceFormatter";

    public static String formatPrice(String price) {
        StringBuilder formattedPrice = new StringBuilder(price);

        //cut leading zeros
        while (formattedPrice.length() > 0 && formattedPrice.charAt(0) == '0') {
            formattedPrice.deleteCharAt(0);
        }

        int indexOfDot = formattedPrice.indexOf(".");
        if (indexOfDot >= 0) {

            //cut ending zeros
            while (formattedPrice.charAt(formattedPrice.length() - 1) == '0') {
                formattedPrice.deleteCharAt(formattedPrice.length() - 1);
            }

            //cut dot in the end
            if (indexOfDot == formattedPrice.length() - 1) {
                formattedPrice.deleteCharAt(formattedPrice.length() - 1);
            }

            //add leading zero, if first symbol is dot
            else if (indexOfDot == 0){
                formattedPrice.insert(0, '0');
            }
        }

        return formattedPrice.toString();
    }

    public static double priceToDouble(String price) {
        String formattedPrice = formatPrice(price);
        if (formattedPrice.isEmpty() || formattedPrice.equals("")) {
            return 0;
        }

        return Double.parseDouble(formattedPrice);
    }
}
