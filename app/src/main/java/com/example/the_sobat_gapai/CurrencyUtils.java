package com.example.the_sobat_gapai;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    public static String formatRupiah(double amount) {
        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(amount);
    }
}