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
import com.itextpdf.text.pdf.ColumnText;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrintPdfActivity extends AppCompatActivity {
    //local defind
    private Calendar newDate = Calendar.getInstance();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    //    final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
    //時間要らない
    final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private String FILE = Environment.getExternalStorageDirectory().toString()
            + "/NID/PDF/" + df.format(newDate.getTime()) + "/" + "Nid" + sdf.format(newDate.getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pdf);
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);

        Bundle extras = getIntent().getExtras();
        String date_sales = extras.getString("date");
        String myResponse = extras.getString("myResponse");
        /*String address1 = getAddress1(myResponse);
        String address2 = getAddress2(myResponse);*/
        HeaderInfo headerInfo = getHeaderInfo(myResponse);
        headerInfo.setKaisyuuDate(date_sales);
        Log.d("PrintPdfActivity", myResponse);
        FILE += "_" + headerInfo.getContractName() + ".pdf";
        File file = new File(FILE);

        try {
            this.createPdf(myResponse, date_sales, headerInfo);
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

    private HeaderInfo getHeaderInfo(String myResponse) {
        HeaderInfo headerInfo = new HeaderInfo();
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                headerInfo.address1 = item.getString("address1");
                headerInfo.address2 = item.getString("address2");
                headerInfo.no = item.getString("employer_name");
                headerInfo.kaisyuuDate = item.getString("address2");
                headerInfo.sama = item.getString("customer_name");
                headerInfo.setContractName(item.getString("contract_altname"));
                break;
            }
            //get pages
            //Check if row < 13*pageNum for fill one per page
            int row = responseArray.length() + 1; //for summary row
            int mod = row % 13;
            int pages = (row - mod) / 13;
            if (pages > 0) {
                pages += 1;
            }
            if (pages == 0) {
                pages = 1;
            }
            headerInfo.setPages(pages);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return headerInfo;
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

    public void createPdf(String myResponse, String dateSales, HeaderInfo headerInfo) throws FileNotFoundException {
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            //define Font
            BaseFont urName = BaseFont.createFont("assets/font/Mincho.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//            Font titleFont = new Font(urName, 16);
            Font urFontName = new Font(urName, 11);
            Font ssFont = new Font(urName, 9);
            Font blankFont = new Font(urName, 5);
            Font sFont = new Font(urName, 10);
            Font mFont = new Font(urName, 14);
//            Document document = new Document(PageSize.A4, 36, 36, 100, 70);
            Document document = new Document(PageSize.A4, 36, 36, 145, 70);
            document.setMarginMirroring(false);
            //store folder
            String root = Environment.getExternalStorageDirectory().toString();
            Log.d("root", root);
            File myDir = new File(root + "/NID/PDF/" + df.format(newDate.getTime()));
            myDir.mkdirs();
            Log.d("FILE", FILE);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
            writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);
            writer.setBoxSize("art", new Rectangle(36, 5, 559, 788));
            writer.setPageEvent(new HeaderFooter(headerInfo));

            document.open();
            PdfPCell cell;

            //header list table
            PdfPTable table = new PdfPTable(6);
//            table.setTotalWidth(new float[]{30, 200, 70, 50, 60, 60, 90});
            table.setTotalWidth(new float[]{30, 220, 90, 60, 70, 90});
            table.setLockedWidth(true);

            //binding list
            int row = 0;
            int sumAmount = 0;
            for (int i = 0; i < responseArray.length(); i++) {
                row = i + 1;
                JSONObject item = responseArray.getJSONObject(i);
                //No
                cell = new PdfPCell(new Phrase(item.getString("setting_no"), urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                //物件名
                cell = new PdfPCell(new Phrase(item.getString("item_name"), urFontName));
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
                if (amount.isEmpty()) {
                    amount = "";
                } else {
                    sumAmount += Integer.parseInt(amount.replaceAll(",", ""));
                }
                cell = new PdfPCell(new Phrase(amount, urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                /*//カウンター差分
                int counterOld = new Integer(item.getString("sales_counter_old"));
                int counter = new Integer(item.getString("sales_counter"));
                int diff = Math.abs(counter - counterOld);
                cell = new PdfPCell(new Phrase(new Integer(diff).toString(), urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);*/
                //メンテカウント
                String salesMemo = item.getString("sales_memo");
//                String salesMemo = "";
                if (salesMemo.isEmpty()) salesMemo = "";
                cell = new PdfPCell(new Phrase(salesMemo, urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                //add summary row
                if (row == responseArray.length()) {
                    cell = new PdfPCell(new Phrase(" ", urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase("合計", urFontName));
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
                    DecimalFormat df = new DecimalFormat("###,###");
                    String sumAmountFormat = df.format(sumAmount);
                    cell = new PdfPCell(new Phrase(sumAmountFormat, urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    /*cell = new PdfPCell(new Phrase(" ", urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);*/
                    cell = new PdfPCell(new Phrase(" ", urFontName));
                    cell.setMinimumHeight(45);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
            }
            //Check if row < 13*pageNum for fill one per page
            row += 1; //for summary row
            int mod = row % 13;
            int page = (row - mod) / 13;
            if (page > 0) {
                page += 1;
            }
            if (page == 0) {
                page = 1;
            }

            while (row < 13 * page) {
                row++;
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
                /*cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);*/
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setMinimumHeight(45);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

//            document.add(prHead);
//            document.add(Chunk.NEWLINE);
//            document.add(titleTable);
//            document.add(Chunk.NEWLINE);
//            document.add(headTable);
//            document.add(Chunk.NEWLINE);
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

    /**
     * Inner class to add a header and a footer.
     */
    class HeaderFooter extends PdfPageEventHelper {
        /**
         * Alternating phrase for the header.
         */
        Phrase[] header = new Phrase[2];
        HeaderInfo headerInfo;

        public HeaderFooter(HeaderInfo headerInfo) {
            this.headerInfo = headerInfo;
        }

        /**
         * Current page number (will be reset for every chapter).
         */
        int pagenumber;

        /**
         * Initialize one of the headers.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         *com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            header[0] = new Phrase("Movie history");
        }

        /**
         * Increase the page number.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
         *com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onStartPage(PdfWriter writer, Document document) {
            pagenumber++;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            addHeader(writer, headerInfo);
            addFooter(writer);
        }

        private void addHeader(PdfWriter writer, HeaderInfo headerInfo) {
            try {
                BaseFont urName = BaseFont.createFont("assets/font/Mincho.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(urName, 16);
                Font urFontName = new Font(urName, 11);
                Font ssFont = new Font(urName, 9);
                Font blankFont = new Font(urName, 5);
                Font sFont = new Font(urName, 10);
                Font mFont = new Font(urName, 14);
                //add Title
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

                //header info
                PdfPTable headTable = new PdfPTable(3);
                headTable.setTotalWidth(new float[]{180, 150, 180});
                headTable.setLockedWidth(true);
                //row1
                cell = new PdfPCell(new Phrase("", urFontName));
                cell.setBorder(Rectangle.NO_BORDER);
                headTable.addCell(cell);
                //No
                cell = new PdfPCell(new Phrase("担当者", sFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                headTable.addCell(cell);

                cell = new PdfPCell(new Phrase(headerInfo.getNo(), sFont));
                cell.setBorder(Rectangle.NO_BORDER);
                headTable.addCell(cell);
                //row2
                cell = new PdfPCell(new Phrase(headerInfo.getSama() + "  様", sFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                headTable.addCell(cell);
                //回収日
                cell = new PdfPCell(new Phrase("回収日", sFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                headTable.addCell(cell);

                cell = new PdfPCell(new Phrase(headerInfo.getKaisyuuDate(), sFont));
                cell.setBorder(Rectangle.NO_BORDER);
                headTable.addCell(cell);

                //header of list table
                PdfPTable table = new PdfPTable(6);
//                table.setTotalWidth(new float[]{30, 200, 70, 50, 60, 60, 90});
                table.setTotalWidth(new float[]{30, 220, 90, 60, 70, 90});
//                table.setTotalWidth(new float[]{40, 250, 100, 80, 90});

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
                /*cell = new PdfPCell(new Phrase("カウンター", urFontName));
                cell.setMinimumHeight(45);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);*/
                cell = new PdfPCell(new Phrase("メンテカウント", urFontName));
                cell.setMinimumHeight(45);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                //add page number
                Rectangle rect = writer.getBoxSize("art");
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_RIGHT, new Phrase(String.format("%d/%d", pagenumber, headerInfo.getPages())),
                        rect.getRight(), rect.getTop() + 20, 0);
/*          ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER, new Phrase(String.format("page %d", pagenumber)),
                    (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);
*/
                titleTable.writeSelectedRows(0, -1, rect.getLeft() + 150, rect.getTop() + 40, writer.getDirectContent());
                headTable.writeSelectedRows(0, -1, rect.getLeft(), rect.getTop(), writer.getDirectContent());
                table.writeSelectedRows(0, -1, rect.getLeft() - 18.6f, rect.getTop() - 46, writer.getDirectContent());

            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }
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
                footer.setTotalWidth(new float[]{370, 190});
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

                cell = new PdfPCell(new Phrase(headerInfo.getAddress1(), ssFont));
                cell.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cell);

                cell = new PdfPCell(new Phrase(headerInfo.getAddress2(), ssFont));
                cell.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cell);

                footer.writeSelectedRows(0, -1, 17, 80, writer.getDirectContent());
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

    public class HeaderInfo {
        private String address1;
        private String address2;
        //No
        private String no;
        //kaisyu date
        private String kaisyuuDate;
        //sama
        private String sama;
        //pages
        private int pages;
        //contract name
        private String contractName;

        public HeaderInfo() {

        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getKaisyuuDate() {
            return kaisyuuDate;
        }

        public void setKaisyuuDate(String kaisyuuDate) {
            this.kaisyuuDate = kaisyuuDate;
        }

        public String getSama() {
            return sama;
        }

        public void setSama(String sama) {
            this.sama = sama;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public String getContractName() {
            return contractName;
        }

        public void setContractName(String contractName) {
            this.contractName = contractName;
        }
    }
}
