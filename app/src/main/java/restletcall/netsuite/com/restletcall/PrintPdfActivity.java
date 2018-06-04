package restletcall.netsuite.com.restletcall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;

import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrintPdfActivity extends AppCompatActivity {
    //local defind
    private Calendar newDate = Calendar.getInstance();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
    private String FILE = Environment.getExternalStorageDirectory().toString()
            + "/NID/PDF/" + df.format(newDate.getTime()) + "/" + "Nid" + sdf.format(newDate.getTime()) + ".pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pdf);
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        File file = new File(FILE);

        Bundle extras = getIntent().getExtras();
        String date_sales = extras.getString("date");
        String myResponse = extras.getString("myResponse");
        String address1 = getAddress1(myResponse);
        String address2 = getAddress2(myResponse);
        Log.d("PrintPdfActivity", myResponse);

        try {
            this.createPdf(myResponse, date_sales, address1, address2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pdfView.fromFile(file).load();
        this.configureToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_print, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = getIntent();
                intent.setClass(this, ViewActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_print:
                File file = new File(FILE);
                try {
                    OpenFile.openFile(PrintPdfActivity.this, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private String getAddress1(String myResponse) {
        String address1 = "";
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                address1 = item.getString("address1");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return address1;
    }

    private String getAddress2(String myResponse) {
        String address2 = "";
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                address2 = item.getString("address2");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return address2;
    }

    private String getCustomerName(String myResponse) {
        String customerName = "";
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                customerName = item.getString("customer_name");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customerName;
    }

    private String getEmployeeName(String myResponse) {
        String employeeName = "";
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                employeeName = item.getString("employer_name");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return employeeName;
    }

    public void createPdf(String myResponse, String dateSales, String address1, String address2) throws FileNotFoundException {
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            //define Font
            BaseFont urName = BaseFont.createFont("assets/font/Mincho.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(urName, 16);
            Font urFontName = new Font(urName, 11);
            Font ssFont = new Font(urName, 9);
            Font sFont = new Font(urName, 10);
            Font mFont = new Font(urName, 14);
            Document document = new Document(PageSize.A4, 36, 36, 50, 110);
            //store folder
            String root = Environment.getExternalStorageDirectory().toString();
            Log.d("root", root);
            File myDir = new File(root + "/NID/PDF/" + df.format(newDate.getTime()));
            myDir.mkdirs();
            //add footer
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
            writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);
            writer.setPageEvent(new MyFooter(address1, address2));

            document.open();
            PdfPCell cell;
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setTotalWidth(new float[]{220});
            titleTable.setLockedWidth(true);
            //Title 回収伝票
            cell = new PdfPCell(new Phrase("回収伝票", titleFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setFixedHeight(30);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);
            titleTable.addCell(cell);

            cell = new PdfPCell(new Phrase("", titleFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setFixedHeight(2);
            cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);
            titleTable.addCell(cell);
            //header
            Paragraph prHead = new Paragraph();
            prHead.setAlignment(Element.ALIGN_JUSTIFIED_ALL);
            prHead.setIndentationLeft(20);
            prHead.setIndentationRight(20);
            prHead.setFont(titleFont);
            prHead.add("");

            PdfPTable headTable = new PdfPTable(3);
            headTable.setTotalWidth(new float[]{170, 110, 100});
            headTable.setLockedWidth(true);
            //row1
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            //No
            cell = new PdfPCell(new Phrase("No.", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);

            cell = new PdfPCell(new Phrase(getEmployeeName(myResponse), ssFont));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            //row2
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            //回収日
            cell = new PdfPCell(new Phrase("回収日", sFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);

            cell = new PdfPCell(new Phrase(dateSales, urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            //row3
            //様
            cell = new PdfPCell(new Phrase(getCustomerName(myResponse) + "様", mFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            //予定日
            cell = new PdfPCell(new Phrase("予定日", sFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);

            //header table
            PdfPTable table = new PdfPTable(7);
            table.setTotalWidth(new float[]{30, 110, 20, 110, 110, 100, 100});
            table.setLockedWidth(true);
            cell = new PdfPCell(new Phrase("No.", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("物件名", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("種別", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("使用料金", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("金額（円）", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("カウンター", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("メンテカウント", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            //binding list
            int row = 0;
            for (int i = 0; i < responseArray.length(); i++) {
                row = i;
                JSONObject item = responseArray.getJSONObject(i);
                if (i < 15) {
                    //No
                    cell = new PdfPCell(new Phrase(new Integer(i+1).toString(), urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //物件名
                    cell = new PdfPCell(new Phrase(item.getString("name"), urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //種別
                    cell = new PdfPCell(new Phrase(item.getString("type"), urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //使用料金
                    cell = new PdfPCell(new Phrase(item.getString("unit_price"), urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //金額（円）
                    String amount = item.getString("sales_counter_d");
                    if (amount.isEmpty()) amount = "";
                    cell = new PdfPCell(new Phrase(amount, urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //カウンター差分
                    int counterOld = new Integer(item.getString("sales_counter_old"));
                    int counter = new Integer(item.getString("sales_counter"));
                    int diff = Math.abs(counter - counterOld);
                    cell = new PdfPCell(new Phrase(new Integer(diff).toString(), urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //メンテカウント
                    String salesMemo = item.getString("sales_memo");
                    if (salesMemo.isEmpty()) salesMemo = "";
                    cell = new PdfPCell(new Phrase(salesMemo, urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
            }
            //Check if row < 9 for fill one page
            while (row < 9){
                row ++;
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            document.add(prHead);
            document.add(Chunk.NEWLINE);
            document.add(titleTable);
            document.add(Chunk.NEWLINE);
            document.add(headTable);
            document.add(Chunk.NEWLINE);
            document.add(table);
            document.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void cretePdf1(String myResponse) throws FileNotFoundException {
        try {
            //define Font
            BaseFont urName = BaseFont.createFont("assets/font/Mincho.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(urName, 16);
            Font urFontName = new Font(urName, 11);
            Font ssFont = new Font(urName, 9);
            Font sFont = new Font(urName, 10);
            Font mFont = new Font(urName, 14);
            Document document = new Document(PageSize.A4, 36, 36, 50, 110);
            //store folder
            String root = Environment.getExternalStorageDirectory().toString();
            Log.d("root", root);
            File myDir = new File(root + "/NID/PDF/" + df.format(newDate.getTime()));
            myDir.mkdirs();
            //add footer
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
            writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);
            writer.setPageEvent(new MyFooter());

            document.open();
            PdfPCell cell;
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setTotalWidth(new float[]{220});
            titleTable.setLockedWidth(true);
            //Title 回収伝票
            cell = new PdfPCell(new Phrase("回収伝票", titleFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setFixedHeight(30);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);

            titleTable.addCell(cell);
            cell = new PdfPCell(new Phrase("", titleFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setFixedHeight(2);
            cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);
            titleTable.addCell(cell);

            Paragraph prHead = new Paragraph();
            prHead.setAlignment(Element.ALIGN_JUSTIFIED_ALL);
            prHead.setIndentationLeft(20);
            prHead.setIndentationRight(20);
            prHead.setFont(titleFont);
            prHead.add("");

            PdfPTable headTable = new PdfPTable(3);
            headTable.setTotalWidth(new float[]{170, 110, 100});
            headTable.setLockedWidth(true);
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("No.", ssFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("回収日", sFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("様", mFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("予定日", sFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            //header table
            PdfPTable table = new PdfPTable(7);
            table.setTotalWidth(new float[]{30, 110, 20, 110, 110, 100, 100});
            table.setLockedWidth(true);
            cell = new PdfPCell(new Phrase("No.", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("物件名", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("種別", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("使用料金", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("金額（円）", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("カウンター", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("メンテカウント", urFontName));
            cell.setMinimumHeight(45);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            //binding list
            for (int i = 0; i < 15; i++) {
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            document.add(prHead);
            document.add(Chunk.NEWLINE);
            document.add(titleTable);
            document.add(Chunk.NEWLINE);
            document.add(headTable);
            document.add(Chunk.NEWLINE);
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    class MyFooter extends PdfPageEventHelper {
        private String address1;
        private String address2;

        public MyFooter(String address1, String address2) {
            this.address1 = address1;
            this.address2 = address2;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            //addHeader(writer);
            addFooter(writer);
        }

        private void addHeader(PdfWriter writer) {
        }

        private void addFooter(PdfWriter writer) {
            try {
                BaseFont urName = BaseFont.createFont("assets/font/Mincho.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(urName, 16);
                Font urFontName = new Font(urName, 11);
                Font ssFont = new Font(urName, 9);
                Font sFont = new Font(urName, 10);
                Font mFont = new Font(urName, 14);
                PdfContentByte cb = writer.getDirectContent();

                PdfPTable footer = new PdfPTable(2);
                footer.setTotalWidth(new float[]{370, 200});
                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapData = stream.toByteArray();
                Image imgSoc = Image.getInstance(bitmapData);
                imgSoc.scaleToFit(120, 120);
                //imgSoc.setAbsolutePosition(20, 20);

                PdfPCell cell = new PdfPCell(imgSoc);
                cell.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cell);
                PdfPTable tableFooter = new PdfPTable(4);
                tableFooter.setTotalWidth(new float[]{10, 50, 10, 50});

                PdfPCell cellTableFooter = new PdfPCell(new Phrase("立会人", ssFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableFooter.addCell(cellTableFooter);

                cellTableFooter = new PdfPCell(new Phrase("㊞", ssFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableFooter.addCell(cellTableFooter);

                cellTableFooter = new PdfPCell(new Phrase("集金人", ssFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableFooter.addCell(cellTableFooter);

                cellTableFooter = new PdfPCell(new Phrase("㊞", ssFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableFooter.addCell(cellTableFooter);

                cell = new PdfPCell(tableFooter);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setRowspan(3);
                footer.addCell(cell);

                cell = new PdfPCell(new Phrase(address1, ssFont));
                cell.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cell);

                cell = new PdfPCell(new Phrase(address2, ssFont));
                cell.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cell);

                /*cell = new PdfPCell(new Phrase("東京本社　／〒164-0012 東京都中野区本町1-23-9　　☎03(3372)1301", ssFont));
                cell.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cell);

                cell = new PdfPCell(new Phrase("横浜営業所／〒233-0045 横浜市磯子区洋光台6-14-1　☎045(831)1301", ssFont));
                cell.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cell);*/

                // get icon
                /*Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapData = stream.toByteArray();
                Image imgSoc = Image.getInstance(bitmapData);
                imgSoc.scaleToFit(120,120);
                imgSoc.setAbsolutePosition(20, 20);
                cb.addImage(imgSoc);*/
                footer.writeSelectedRows(0, -1, 20, 100, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            } catch (MalformedURLException e) {
                throw new ExceptionConverter(e);
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    public class Rotate extends PdfPageEventHelper {
        protected PdfNumber rotation = PdfPage.PORTRAIT;

        public void setRotation(PdfNumber rotation) {
            this.rotation = rotation;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            writer.addPageDictEntry(PdfName.ROTATE, rotation);
        }
    }
}
