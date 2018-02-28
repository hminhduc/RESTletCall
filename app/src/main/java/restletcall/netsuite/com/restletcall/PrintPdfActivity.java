package restletcall.netsuite.com.restletcall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrintPdfActivity extends AppCompatActivity {

    private Calendar newDate = Calendar.getInstance();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private String FILE = Environment.getExternalStorageDirectory().toString()
            +"/NID/PDF/"+"Nid"+sdf.format(newDate.getTime())+".pdf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pdf);
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        File file = new File(FILE);
        try {
            cretePdf();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pdfView.fromFile(file).load();
    }


    public void cretePdf() throws FileNotFoundException {
        try {
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            BaseFont urName = BaseFont.createFont("assets/font/Mincho.otf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            Font titleFont = new Font(urName, 20);
            Font urFontName = new Font(urName, 12);
            Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Document document = new Document(PageSize.A4.rotate());

            String root = Environment.getExternalStorageDirectory().toString();
            Log.d("root", root);
            File myDir = new File(root + "/NID/PDF");
            myDir.mkdirs();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
            writer.setPageEvent(new MyFooter());

            document.open();
            Paragraph prHead = new Paragraph();
            prHead.setAlignment(Element.ALIGN_CENTER);
            prHead.setFont(titleFont);
            prHead.add("回収伝票");
            PdfPTable headTable = new PdfPTable(2);
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("担当者:EMP00001 古川", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("虎の門病院　様", urFontName));
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);
            cell = new PdfPCell(new Phrase("回収日 2018年2月2日", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            headTable.addCell(cell);


            PdfPTable table = new PdfPTable(10);
            table.setTotalWidth(new float[]{ 40, 100, 100, 40 , 60, 60, 60, 60, 60, 60 });
            table.setLockedWidth(true);
            cell = new PdfPCell(new Phrase("No.", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("物件名", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("シリアルNo.", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("種別", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("使用料金", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("前回カウンタ", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("今回カウンタ", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("カウンタ差分", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("金額", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("メンテカウント", urFontName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            for(int i= 0; i< 2; i++){
                cell = new PdfPCell(new Phrase("001", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("小型洗濯機", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("AAA000111", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("コ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("300", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("100", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("110", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("10", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("3000", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("-1", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            for(int i= 0; i< 2; i++){
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", urFontName));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            document.add(prHead);
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

    class MyFooter extends PdfPageEventHelper {

        public void onEndPage(PdfWriter writer, Document document) {
            addHeader(writer);
        }

        private void addHeader(PdfWriter writer){
            try {
                // set defaults
                PdfContentByte cb = writer.getDirectContent();
                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapData = stream.toByteArray();
                Image imgSoc = Image.getInstance(bitmapData);
                imgSoc.scaleToFit(120,120);
                imgSoc.setAbsolutePosition(20, 20);
                cb.addImage(imgSoc);
            } catch(DocumentException de) {
                throw new ExceptionConverter(de);
            } catch (MalformedURLException e) {
                throw new ExceptionConverter(e);
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }

        private void addFooter(PdfWriter writer){
            PdfPTable footer = new PdfPTable(3);
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
