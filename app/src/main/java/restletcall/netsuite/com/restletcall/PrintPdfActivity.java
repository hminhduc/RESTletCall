package restletcall.netsuite.com.restletcall;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                    | Font.UNDERLINE, BaseColor.GRAY);
            Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Document document = new Document(PageSize.A4);

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/PDF");
            myDir.mkdirs();

            PdfWriter.getInstance(document, new FileOutputStream(FILE));

            document.open();
            Paragraph prProfile = new Paragraph();
            prProfile.setFont(smallBold);
            prProfile.add("\n \n ID : "+id+"\n");
            prProfile.setFont(normal);
            prProfile.add("\nTitle : "+title+ "\nCustomer : "+customer+"\n");

            prProfile.setFont(smallBold);
            document.add(prProfile);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
