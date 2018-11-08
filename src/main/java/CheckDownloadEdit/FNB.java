package CheckDownloadEdit;


import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FNB {

    public static void main(String[] args)throws Exception {

//        //Blank Document
//        XWPFDocument document = new XWPFDocument();
//
//        //Write the Document in file system
//        FileOutputStream out = new FileOutputStream( new File("D:\\MKR_DIR\\createdocument.docx"));
////create Paragraph
//        XWPFParagraph paragraph = document.createParagraph();
//        XWPFRun run = paragraph.createRun();
//        run.setText("At tutorialspoint.com, we strive hard to " +
//                        "provide quality tutorials for self-learning " +
//                        "purpose in the domains of Academics, Information " +
//                        "Technology, Management and Computer Programming Languages.");
//
//                document.write(out);
//        out.close();
//        System.out.println("createparagraph.docx written successfully");


        XWPFDocument docx = new XWPFDocument(new FileInputStream(new File("D:\\MKR_DIR\\Obem_sredstv_Fonda_natsionalnogo_blagosostoyaniya_01_10_2018.docx")));

        //using XWPFWordExtractor Class
        XWPFWordExtractor we = new XWPFWordExtractor(docx);
        String[] lines = we.getText().split("\n");
        for (int i = 5; i < 60; i++) {
            System.out.println(lines[i]);
        }
    }

}
