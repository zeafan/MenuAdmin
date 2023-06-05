package com.zeafan.loginactivity.activity;

import androidx.appcompat.app.AppCompatActivity;
import ru.katso.livebutton.LiveButton;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.core.GlobalClass;
import com.zeafan.loginactivity.core.Utilities;
import com.zeafan.loginactivity.data.ProductItem;
import com.zeafan.loginactivity.data.User;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class MapActivity extends AppCompatActivity {
    public static String list_key = "key_file";
    File excelFile;
    formatType type;
    ArrayList<Row> rows;
    ArrayList<String> Header;
    LiveButton btn_confirm;
    Spinner sp_name,sp_latin_name,sp_more_details,sp_unit,sp_price,sp_barcode,sp_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getDataFromIntent();
        rows = read(excelFile);
        Header =convertToArraListCell(rows.get(0).cellIterator());
        initView();
        if(Header.size()>0){
            rows.remove(0);
            Header.add(0,getString(R.string.without));
            setValueInSpinner();
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ProductItem>productItems = new ArrayList<>();
                int name_index =sp_name.getSelectedItemPosition();
                int latin_name_index =sp_latin_name.getSelectedItemPosition();
                int more_details_index =sp_more_details.getSelectedItemPosition();
                int unit_index =sp_unit.getSelectedItemPosition();
                int price_index =sp_price.getSelectedItemPosition();
                int barcode_index =sp_barcode.getSelectedItemPosition();
                int path_index =sp_path.getSelectedItemPosition();
                for(Row row : rows){
                    try {
                        ProductItem productItem = new ProductItem(ProductItem.getString(row,name_index),
                                UUID.randomUUID().toString(),
                                ProductItem.getString(row,latin_name_index),
                                String.valueOf(ProductItem.getDouble(row,barcode_index)),
                                ProductItem.getDouble(row,price_index),
                                ProductItem.getString(row,path_index),
                                ProductItem.getString(row,more_details_index),
                                ProductItem.getString(row,unit_index)
                                );
                        productItems.add(productItem);
                    }catch (Exception e){

                    }
            }
                Dialog d =  Utilities.showWaitDialog(MapActivity.this);
                sendproductItems(d,productItems);
            }
        });
    }

    private void sendproductItems(Dialog d, ArrayList<ProductItem> productItems) {
        for(ProductItem item : productItems)
        {
            FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    .child(User.key_firebase).child(ProductItem.Key_firebase_list)
                    .child(item.ItemGuid).setValue(item);
        }
        d.dismiss();
    }

    private void setValueInSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Header);
        sp_name.setAdapter(adapter);
        sp_latin_name.setAdapter(adapter);
        sp_more_details.setAdapter(adapter);
        sp_unit.setAdapter(adapter);
        sp_price.setAdapter(adapter);
        sp_barcode.setAdapter(adapter);
        sp_path.setAdapter(adapter);
    }

    private void initView() {
        sp_name = findViewById(R.id.sp_name);
        sp_latin_name = findViewById(R.id.sp_latin_name);
        sp_more_details = findViewById(R.id.sp_more_details);
        sp_unit = findViewById(R.id.sp_unit_name);
        sp_price = findViewById(R.id.sp_price);
        sp_barcode = findViewById(R.id.sp_barcode);
        sp_path = findViewById(R.id.sp_image_path);
        btn_confirm = findViewById(R.id.btn_confrim);
    }

    private ArrayList<String> convertToArraListCell(Iterator<Cell> cellIterator) {
        ArrayList<String> rowValues = new ArrayList<>();
        while (cellIterator.hasNext())
        {
            Cell cell = cellIterator.next();
            rowValues.add(cell.getStringCellValue());
        }
        return rowValues;
    }

    public ArrayList<Row> read(File ExcelFile) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(ExcelFile);
            if (type.equals(formatType.xlsx)) {
                XSSFWorkbook xwb = null;
                try {
                    xwb = new XSSFWorkbook(fileInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //creating a Sheet object to retrieve the object
                if (xwb == null) return null;
                return convertToArraList(xwb.getSheetAt(0));
            } else {
                HSSFWorkbook hwb = null;
                try {
                    hwb = new HSSFWorkbook(fileInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //creating a Sheet object to retrieve the object
                if (hwb == null) return null;
                return convertToArraList(hwb.getSheetAt(0));
            }
        }catch (Exception e){
        }
        return null;
    }

    private ArrayList<Row> convertToArraList(Iterable<Row> rows) {
        ArrayList<Row> r= new ArrayList<>();
        for(Row row :rows){
            r.add(row);
        }
        return r;
    }


//        Workbook workbook = null;
//        try {
//            WorkbookSettings ws = new WorkbookSettings();
//            ws.setGCDisabled(true);
//
//
//
//            workbook = Workbook.getWorkbook(ExcelFile, ws);
//            Sheet sheet = workbook.getSheet(0);
//
//            int rowCount = sheet.getRows();
//            String[][] result = new String[rowCount][];
//            for (int i = 0; i < rowCount; i++) {
//                Cell[] row = sheet.getRow(i);
//
//                result[i] = new String[row.length];
//                for (int j = 0; j < row.length; j++) {
//                    result[i][j] = row[j].getContents();
//                }
//            }
//            return result;
//
//
//        } catch (BiffException e) {
//
//        } catch (IOException e) {
//        } catch (Exception e) {
//        } finally {
//            if (workbook != null) {
//                workbook.close();
//            }
//        }


    private void getDataFromIntent() {
        excelFile = (File) getIntent().getSerializableExtra(list_key);
        type = excelFile.getAbsolutePath().contains("xlsx")?formatType.xlsx:formatType.xls;
    }
    enum formatType{
        xlsx,
        xls
    }
}