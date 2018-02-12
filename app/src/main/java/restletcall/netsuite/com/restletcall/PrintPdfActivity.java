package restletcall.netsuite.com.restletcall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class PrintPdfActivity extends AppCompatActivity {

    private String FILE = Environment.getExternalStorageDirectory().toString()
            +"/PDF/"+"Name.pdf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pdf);
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        File file = new File(FILE);
        pdfView.fromFile(file).load();
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        String title = extras.getString("title");
        String customer = extras.getString("customer");
        try {
            cretePdf(id, title, customer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void cretePdf(String id, String title, String customer) throws FileNotFoundException {
        try {

            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            BaseFont urName = BaseFont.createFont("assets/font/Mincho.otf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            Font urFontName = new Font(urName, 12);
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                    | Font.UNDERLINE, BaseColor.GRAY);
            Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Document document = new Document(PageSize.A4);

            String root = Environment.getExternalStorageDirectory().toString();
            Log.d("root", root);
            File myDir = new File(root + "/PDF");
            myDir.mkdirs();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
            writer.setPageEvent(new MyFooter());
            document.open();
            Paragraph prProfile = new Paragraph();
            prProfile.setFont(urFontName);
            prProfile.add("\n \nID : "+id+"\n");
            prProfile.setFont(urFontName);
            prProfile.add("\nTitle : "+title+ "\nCustomer : "+customer+"\n");

            prProfile.setFont(smallBold);
            document.add(prProfile);

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
                imgSoc.scaleToFit(110,110);
                imgSoc.setAbsolutePosition(390, 800);
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
}
