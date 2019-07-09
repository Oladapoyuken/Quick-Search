/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quick_search;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import jfxtras.scene.control.ImageViewButton;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.controlsfx.control.CustomTextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Yuken4real
 */
public class FXMLDocumentController implements Initializable {
    
    public File choosefile, f, preFile = null;
    public boolean check = false, threadState = true, threadState1 = true, threadState2 = true, barCheck;
    public int x, b, pageNumber;
    public String word, wordSmal, wordBig, wordNorm, ext = "doc", ext1 = "pdf", ext2 = "txt", ext3 = "docx", ext4 = "html";
    
    public List<File> list = new ArrayList<>();
    public List<File> list1 = new ArrayList<>();
    public List<File> list2 = new ArrayList<>();
    public List<File> list3 = new ArrayList<>();
    
    public SortedSet<File> set = new TreeSet<>();
    public SortedSet<File> set1 = new TreeSet<>();
    public SortedSet<File> set2 = new TreeSet<>();
    public SortedSet<File> set3 = new TreeSet<>();
    public SortedSet<File> set4 = new TreeSet<>();
    
    public ObservableList<File> displayList;
    public ObservableList<String> comboItems;
    
    
    
    
    @FXML
    private Label note;
    
    @FXML
    private JFXListView<File> displayShow;
    
    @FXML
    private JFXButton folderSeek, contentSeek, fileSeek, aboutSeek;
    
    @FXML
    private ImageViewButton changeBut;
    
    @FXML
    private AnchorPane contentPane, folderPane, filePane, listPane, aboutPane, frame;
   
    @FXML
    private CustomTextField folderField, fileField, contentField, preFileLocation ;
    
    @FXML
    private ComboBox combo = new ComboBox();//.getItems().addAll("All file types", "PDF file type only", "MS-word file type only","text documents only");
    @FXML
    private Button fileBut, folderBut, contentBut, stopBut, resetBut;
    @FXML
    private ProgressBar proBar;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        folderPane.setVisible(true);
        filePane.setVisible(false);
        contentPane.setVisible(false);
        aboutPane.setVisible(false);
        listPane.setVisible(false);
        combo.setDisable(true);
        pageNumber = 0;
        
        folderSeek.setTextFill(Color.rgb(255, 255, 0));
        fileSeek.setTextFill(Color.rgb(255, 255, 255));
        contentSeek.setTextFill(Color.rgb(255, 255, 255));
        aboutSeek.setTextFill(Color.rgb(255, 255, 255));
       
        proBar.setVisible(false);
       
        comboItems = FXCollections.observableArrayList(
                "All file types", "PDF file type only", "MS-word file type only","Text documents only","Html files only");
        combo.getItems().addAll(comboItems);
        combo.setValue("All file types");
        
        displayList = FXCollections.observableArrayList();
        displayShow.setItems(displayList);
        displayShow.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        displayShow.depthProperty().set(4);
        displayShow.setExpanded(true);
        displayList.addListener((ListChangeListener.Change<? extends File> c) -> {
        });
        displayShow.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends File> observable, File oldValue, File newValue) -> {
            Desktop openFile = Desktop.getDesktop();
            try {
                if (newValue.canExecute()) {
                    openFile.open(newValue);        
                } 
                else {
                    note.setText("Sorry, cannot open this file");
                }
                //displayShow.getSelectionModel().clearSelection();
                //System.out.println(displayShow.getSelectionModel().getSelectedIndex());
            }
            catch (Exception e) {
                }
            
            
        });
        
    }  
    
    
    
    @FXML
    private void choose(MouseEvent event) {
        chooseFile();
    }
    
    @FXML
    private void Reset(ActionEvent event) {
        resetSearch();
    }
    
    @FXML
    private void Cancel(ActionEvent event) {
        cancelSearch();
    }
    
    private void seek(ActionEvent event) {
        //System.out.println("Started");
        gotoSearch();
    }  
    
    @FXML
    private void comboSelection(ActionEvent event) {
        Selection(getSelectedIndex());
    }
    
    @FXML
    private void Checker(ActionEvent event) {
        gotoSearch();
    }
    
    
    @FXML
    private void PageSwitch(ActionEvent event) {
        preFile = null;
        cancelSearch();
//        resetSearch();
        check = false;
        displayList.clear();
        if(event.getSource() == folderSeek ){
            pageNumber = 0;
            Page(0);
        }
        else if(event.getSource() == fileSeek){
            pageNumber = 1;
            Page(1);
        }
        else if(event.getSource() == contentSeek){
            pageNumber = 2;
            Page(2);
        }
        else if(event.getSource() == aboutSeek){
            pageNumber = 3;
            double w = frame.getWidth();
            double h = frame.getHeight();
            //aboutPane.set
            Page(3);
        }
    }
    
    public void Page(int k){
        stopBut.setDisable(true);
        resetBut.setDisable(true);
        changeBut.setDisable(false);
        
//        proBar.setVisible(false);
//        changeBut.setDisable(true);
//        
//        threadState = false;
//        threadState1 = false;
//        threadState2 = false;
            
        note.setText("Enter new word");
        preFileLocation.clear();
        check = false;
        switch (k){
            case 0 :{
                folderSeek.setTextFill(Color.rgb(255, 255, 0));
                fileSeek.setTextFill(Color.rgb(255, 255, 255));
                contentSeek.setTextFill(Color.rgb(255, 255, 255));
                aboutSeek.setTextFill(Color.rgb(255, 255, 255));
                folderPane.setVisible(true);
                filePane.setVisible(false);
                contentPane.setVisible(false);
                aboutPane.setVisible(false);
                listPane.setVisible(false);
                combo.setDisable(true);
                pageNumber = 0;
                folderField.clear();
                break;
            }
            case 1 :{
                fileSeek.setTextFill(Color.rgb(255, 255, 0));
                folderSeek.setTextFill(Color.rgb(255, 255, 255));
                contentSeek.setTextFill(Color.rgb(255, 255, 255));
                aboutSeek.setTextFill(Color.rgb(255, 255, 255));
                folderPane.setVisible(false);
                filePane.setVisible(true);
                contentPane.setVisible(false);
                aboutPane.setVisible(false);
                listPane.setVisible(false);
                combo.setDisable(false);
                pageNumber = 1;
                fileField.clear();
                break;
            }
            case 2 :{
                contentSeek.setTextFill(Color.rgb(255, 255, 0));
                fileSeek.setTextFill(Color.rgb(255, 255, 255));
                folderSeek.setTextFill(Color.rgb(255, 255, 255));
                aboutSeek.setTextFill(Color.rgb(255, 255, 255));
                folderPane.setVisible(false);
                filePane.setVisible(false);
                contentPane.setVisible(true);
                aboutPane.setVisible(false);
                listPane.setVisible(false);
                pageNumber = 2;
                combo.setDisable(false);
                contentField.clear();
                break;
            }
            case 3 :{
                contentSeek.setTextFill(Color.rgb(255, 255, 255));
                fileSeek.setTextFill(Color.rgb(255, 255, 255));
                folderSeek.setTextFill(Color.rgb(255, 255, 255));
                aboutSeek.setTextFill(Color.rgb(255, 255, 0));
                folderPane.setVisible(false);
                filePane.setVisible(false);
                contentPane.setVisible(false);
                aboutPane.setVisible(true);
                listPane.setVisible(false);
                pageNumber = 3;
                
                break;
            }
            default :{
                
            }
        }
    }
    
    
    
    
    
    
    private void gotoSearch() {
        if(!contentField.getText().trim().isEmpty() || !folderField.getText().trim().isEmpty()
                || !fileField.getText().trim().isEmpty()){
            threadState = true;
            threadState1 = true;
            threadState2 = true;
            
            word = "";
            displayList.clear();
            note.setText("");
            
            f = null;
            try {
                if (pageNumber == 2) {
                    if (!contentField.getText().trim().isEmpty()) {
                        contentPane.setVisible(false);
                        listPane.setVisible(true);
                        word = contentField.getText();
                        wordSmal = word.toLowerCase();
                        wordBig = word.toUpperCase();
                        wordNorm = word.replaceFirst(word.substring(0, 1), word.substring(0, 1).toUpperCase());
                    }
                    else{
                        note.setText("Please insert a word");
                    }
                }
                
                if(pageNumber == 1){
                    Generalmethods(fileField, filePane);
                }
                else if(pageNumber == 0){
                    Generalmethods(folderField, folderPane);
                }
                if (preFile == null) {
                    f = chooseFile();
                    if ((f.isDirectory() || f.isFile()) && f.isAbsolute()) {
                        displayList.clear();
                        note.setText("");
                        preFile = f;
                        preFileLocation.setText(preFile.getName());
                        changeBut.setDisable(true);
                        stopBut.setDisable(false);
                        resetBut.setDisable(false);
                        new Thread(new startSearch()).start();
                    }
                    else{
                        note.setText("Invalid Directory");
                    }
                }
                else{
                    changeBut.setDisable(true);
                    stopBut.setDisable(false);
                    resetBut.setDisable(false);
                    preFileLocation.setText(preFile.getName());
                    f = preFile;
                    new Thread(new startSearch()).start();
                }        
                 
            } 
            catch (Exception e) {
            }
        }
        else{
            note.setText("please insert a word");
        }
    }

    private void Generalmethods(CustomTextField fileField, AnchorPane filePane) {
        if(!fileField.getText().trim().isEmpty()){
            word = fileField.getText();
            filePane.setVisible(false);
            listPane.setVisible(true);
            word = fileField.getText();
            wordSmal = word.toLowerCase();
            wordBig = word.toUpperCase();
            wordNorm = word.replaceFirst(word.substring(0, 1), word.substring(0, 1).toUpperCase());
        }
        else{
            note.setText("Please insert a word");
        }
    }


    private File chooseFile(){
        choosefile = null;
        preFile = null;
        check = true;
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select Directory ");
        File tempFile = dc.showDialog(null);
        try {
            if (tempFile != null) {
                choosefile = tempFile;
                preFile = tempFile;
                preFileLocation.setText(preFile.getName());
                
            } else {
                note.setText("Operation cancelled");
                check = false;
                
            }
        } catch (Exception e) {
        }
        return choosefile;
    }
    
    public int readHtml(String fileName) {
        b = 0;
        try {
            File htmlDoc = new File(fileName);
            Document doc = Jsoup.parse(htmlDoc, null);
            String found = doc.body().text();
            if (found.contains(wordNorm) || found.contains(wordBig) || found.contains(wordSmal)) {
                b++;
            }
        } catch (IOException ioe) {
        }
        return b;
    }
    
    public int readDocFile(String fileName) {
        b = 0;
        try {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            HWPFDocument doc = new HWPFDocument(fis);
            WordExtractor we = new WordExtractor(doc);
            String found = we.getText();
            if (found.contains(wordNorm) || found.contains(wordBig) || found.contains(wordSmal)) {
                b++;
            }
        } catch (Exception e) {
        }
        return b;
    }

    public int pdFind(String location) {
        x = 0;
        try {
            Pattern pattern = Pattern.compile(wordSmal);
            Pattern pattern1 = Pattern.compile(wordNorm);
            Pattern pattern2 = Pattern.compile(wordNorm);
            PDDocument document = null;
            document = PDDocument.load(new File(location));

            document.getClass();
            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                PDFTextStripper Tstripper = new PDFTextStripper();
                try (Scanner scan = new Scanner(Tstripper.getText(document))) {
                    while (scan.findWithinHorizon(pattern, 0) != null || scan.findWithinHorizon(pattern1, 0) != null || scan.findWithinHorizon(pattern2, 0) != null) {
                        x++;
                    }
                }
                document.close();
            }
        } catch (IOException e) {
        }
        return x;

    }

    public int chkTxt(String word, String path) throws Exception {
        int c = 0;
        Pattern pattern = Pattern.compile(wordSmal);
        Pattern pattern1 = Pattern.compile(wordNorm);
        Pattern pattern2 = Pattern.compile(wordBig);
        File file = new File(path);
        try (Scanner scan = new Scanner(file)) {
            while (scan.findWithinHorizon(pattern, 0) != null || scan.findWithinHorizon(pattern1, 0) != null || scan.findWithinHorizon(pattern2, 0) != null) {
                c++;
            }
        }
        return c;
    }

    public int readDocxFile(String location) {
        b = 0;
        try {
            XWPFDocument dx = new XWPFDocument(new FileInputStream(location));
            XWPFWordExtractor we = new XWPFWordExtractor(dx);
            if (we.getText().contains(wordBig) || we.getText().contains(wordSmal) || we.getText().contains(wordNorm)) {
                b++;
            }
        } catch (Exception e) {
        }
        return b;
    }
    
    
    public void print(SortedSet<File> tempSet) {
        list.clear();
        sortToListMethod(tempSet, list);

    }                                                                                   

    public void print1(SortedSet<File> tempSet) {                                       
                                                                                                
        list1.clear();
        sortToListMethod(tempSet, list1);
    }

    private void sortToListMethod(SortedSet<File> tempSet, List<File> list1) {
        try {
            Iterator<File> iterator = tempSet.iterator();
            while (iterator.hasNext()) {
                File f = iterator.next();
                list1.add(f);
            }
        } catch (Exception e) {
        }
    }

    public void print2(SortedSet<File> tempSet) {
        list2.clear();
        sortToListMethod(tempSet, list2);

    }

    public void print3(SortedSet<File> tempSet) {
        sortToListMethod(tempSet, list);
    }
    
    public void print4(SortedSet<File> tempSet) {
        sortToListMethod(tempSet, list3);
    }
    
    
    
    public void display() {
        print(set);
        print1(set1);
        print2(set2);
        print3(set3);
        print4(set4);

    }
    
    
    public void Selection(int sel) {
        switch (sel) {
            case 4: {
                displayList.clear();
                displayList.addAll(list3);
                displayShow.getSelectionModel().clearSelection();
                if (displayList.isEmpty()) {
                    note.setText("Enter new word");
                } else {
                    note.setText(displayList.size() + " result found for '" + word + "'");
                }
                break;
            }
            case 3: {
                displayList.clear();
                displayList.addAll(list2);
                displayShow.getSelectionModel().clearSelection();
                if (displayList.isEmpty()) {
                    note.setText("Enter new word");
                } else {
                    note.setText(displayList.size() + " result found for '" + word + "'");
                }
                break;
            }
            case 2: {
                displayList.clear();
                displayList.addAll(list);
                displayShow.getSelectionModel().clearSelection();
                if (displayList.isEmpty()) {
                    note.setText("Enter new word");
                } else {
                    note.setText(displayList.size() + " result found for '" + word + "'");
                }
                break;
            }
            case 1: {
                displayList.clear();
                displayList.addAll(list1);
                displayShow.getSelectionModel().clearSelection();
                if (displayList.isEmpty()) {
                    note.setText("Enter new word");
                } else {
                    note.setText(displayList.size() + " result found for '" + word + "'");
                }
                break;
            }
            case 0: {
                displayList.clear();
                displayList.addAll(list);
                displayList.addAll(list1);
                displayList.addAll(list2);
                displayList.addAll(list3);
                displayShow.getSelectionModel().clearSelection();
                if (displayList.isEmpty()) {
                    note.setText("Enter new word");
                } else {
                    note.setText(displayList.size() + " result found for '" + word + "'");
                }
                break;
            }
            default:{
                
            }
        }
    }
    
    
    public int getSelectedIndex(){
        int comboIndex = 0;
        String temp = combo.getValue().toString();
        switch (temp) {
            case "All file types":
                comboIndex = 0;
                break;
            case "PDF file type only":
                comboIndex = 1;
                break;
            case "MS-word file type only":
                comboIndex = 2;
                break;
            case "Text documents only":
                comboIndex = 3;
                break;
            case "Html files only":
                comboIndex = 4;
                break;
            default:
                break;
        }
        return comboIndex;
    }

    
    
    private class startSearch extends Task<Boolean>{

        @Override
        protected Boolean call(){
            try {
                Platform.runLater(() ->{
                    note.setText("searching for '" + word + "'");
                    combo.setDisable(true);
                    proBar.setVisible(true);
                });
                if (pageNumber == 2) {
                    search2(f, getSelectedIndex());
                } 
                else if(pageNumber == 1){
                    search1(f, getSelectedIndex());
                }
                else if(pageNumber == 0){
                    search(f);
                }
            } catch (Exception e) {
            }
            return true;
        }
        @Override
        protected void succeeded(){
            try {
                if(get()){
                    note.setText(displayList.size() + " result found for '" + word + "'");
                    proBar.setVisible(false);
                    combo.setDisable(false);
                    display();    
                }
            } catch (InterruptedException | ExecutionException ex) {
            }
        }
        @Override
        public boolean cancel(boolean mayInterruptIfRunning){
            return super.cancel(mayInterruptIfRunning);
        }
    }
    
    
    
    private void cancelSearch(){
        displayShow.getSelectionModel().clearSelection();
        threadState = false;
        threadState1 = false;
        threadState2 = false;
        proBar.setVisible(false);
        changeBut.setDisable(true);
//        if(!displayList.isEmpty()) {
//                note.setText(displayList.size() + " result found for '" + word + "'");
//            } 
//        else {
//                note.setText("Reset to enter new search");
//            }
    }
    private void resetSearch(){
        threadState = false;
        threadState1 = false;
        threadState2 = false;
        stopBut.setDisable(true);
        resetBut.setDisable(true);
        changeBut.setDisable(false);
        note.setText("Enter new word");
        list.clear();
        list1.clear();
        list2.clear();
        list3.clear();
        set.clear();
        set1.clear();
        set2.clear();
        set3.clear();
        set4.clear();
        displayList.clear();
        check = true;
        switch (pageNumber) {
            case 0:
                combo.setDisable(true);
                listPane.setVisible(false);
                folderPane.setVisible(true);
                folderField.clear();
                break;
            case 1:
                combo.setDisable(false);
                listPane.setVisible(false);
                filePane.setVisible(true);
                fileField.clear();
                break;
            case 2:
                combo.setDisable(false);
                listPane.setVisible(false);
                contentPane.setVisible(true);
                contentField.clear();
                break;
            default:
                break;
        }
    }
    
    //FOLDER
    private synchronized void search(File file) throws IOException{
        while(true){
            try {
                Path p = Paths.get(file.toURI());
                if (file.isDirectory() && !Files.isHidden(p) && Files.isReadable(p)) {
                    File[] files = file.listFiles();
                    List<File> contents = new ArrayList<>(Arrays.asList(files));
                    
                    if (files.length > 0) {
                        contents.stream().forEach(tempFiles -> {
                            if (tempFiles.isDirectory()) {
                                String post;
                                post = tempFiles.getName();
                                if (post.contains(wordSmal) || post.contains(wordNorm) || post.contains(wordBig) ) {
                                    Platform.runLater(() -> {
                                        displayList.add(tempFiles);
                                    });
                                }
                                if (tempFiles.isDirectory()) {
                                    try {
                                        search(tempFiles);
                                    } catch (IOException ex) {
                                    }
                                }
                            }
                            
                        });
                    }
                } 
            } catch (Exception e) {
            }
            
            break;
        }
    }
    
    
    //Files
    private synchronized void search1(File file, int check) {
        String sub1 = file.getName().substring(0, file.getName().length()-4);
        while (true) {
            Path p = Paths.get(file.toURI());
            try {
                
                if (file.isDirectory() && !Files.isHidden(p) && Files.isReadable(p)) {
                    File[] content = file.listFiles();
                    List<File> files = new ArrayList<>(Arrays.asList(content));
                    try {
                        files.stream().forEach(temp -> {
                            String sub = temp.getName().substring(0, temp.getName().length()-4);
                            //System.out.println("Searching for" + sub);
                            if (threadState1 == false) {
                                files.clear(); 
                            }

                            if (temp.isDirectory()) {
                                search1(temp, getSelectedIndex());
                            } 
                            else {
                                directoryCheck(check, temp, sub);
                            }

                        });
                    } catch (Exception e) {
                    }
                    if (threadState1 == false) {
                        files.clear();
                        break;
                    }
                } 
                else directoryCheck(check, file, sub1);
                if (threadState1 == false) {
                        
                        break;
                    }
            } catch (IOException iOException) {
            }
            break;
        }
    }

    private void directoryCheck(int check, File temp, String sub) {
        if (temp.getName().endsWith(ext) && (check == 0 || check == 2)) {
            if (sub.contains(wordSmal) || sub.contains(wordBig) || sub.contains(wordNorm)) {
                set.add(temp);
                Platform.runLater(() -> {
                    displayList.add(temp);
                });
            }
        }
        else if (temp.getName().endsWith(ext1) && (check == 0 || check == 1)) {
            if (sub.contains(wordSmal) || sub.contains(wordBig) || sub.contains(wordNorm)) {
                set1.add(temp);
                Platform.runLater(() -> {
                    displayList.add(temp);
                });
            }
        }
        else if (temp.getName().endsWith(ext2) && (check == 0 || check == 3)) {

            if (sub.contains(wordSmal) || sub.contains(wordBig) || sub.contains(wordNorm)) {
                set2.add(temp);
                Platform.runLater(() -> {
                    displayList.add(temp);
                });
            }
        }
        else if (temp.getName().endsWith(ext3) && (check == 0 || check == 2)) {

            if (sub.contains(wordSmal) || sub.contains(wordBig) || sub.contains(wordNorm)) {
                set3.add(temp);
                Platform.runLater(() -> {
                    displayList.add(temp);
                });
            }
        }
        else if (temp.getName().endsWith(ext4) && (check == 0 || check == 4)) {

            if (sub.contains(wordSmal) || sub.contains(wordBig) || sub.contains(wordNorm)) {
                set4.add(temp);
                Platform.runLater(() -> {
                    displayList.add(temp);
                });
            }
        }
    }


    //CONTENTS
    private synchronized void search2(File file, int check) {
        while (true) {
            Path p = Paths.get(file.toURI());
            try {
                
                if (file.isDirectory() && !Files.isHidden(p) && Files.isReadable(p)) {
                    File[] content = file.listFiles();
                    List<File> files = new ArrayList<>(Arrays.asList(content));
                    
                    
                    try {
                        files.stream().forEach(temp -> {
                            if (threadState2 == false) {
                                files.clear();   
                                
                            }

                            if (temp.isDirectory()) {
                                search2(temp, getSelectedIndex());
                            } else if (temp.getName().endsWith(ext) && (check == 0 || check == 2)) {
                                try {
                                    if (readDocFile(temp.getAbsolutePath()) > 0) {
                                        set.add(temp);

                                        Platform.runLater(()->{
                                            displayList.add(temp);
                                        });

                                    }


                                } catch (Exception e) {

                                }
                            } else if (temp.getName().endsWith(ext1) && (check == 0 || check == 1)) {

                                try {

                                    if (pdFind(temp.getAbsolutePath()) > 0) {
                                        set1.add(temp);
                                        Platform.runLater(()->{
                                            displayList.add(temp);
                                        });                                    }
                                } catch (Exception e) {

                                }

                            } else if (temp.getName().endsWith(ext2) && (check == 0 || check == 3)) {

                                try {
                                    if (chkTxt(word, temp.getAbsolutePath()) > 0) {
                                        set2.add(temp);
                                        Platform.runLater(()->{
                                            displayList.add(temp);
                                        });
                                    }
                                } catch (Exception e) {
                                }
                            } else if (temp.getName().endsWith(ext3) && (check == 0 || check == 2)) {

                                try {

                                    if (readDocxFile(temp.getAbsolutePath()) > 0) {
                                        set3.add(temp);
                                        Platform.runLater(()->{
                                            displayList.add(temp);
                                        });
                                    }
                                } catch (Exception e) {

                                }

                            } else if (temp.getName().endsWith(ext4) && (check == 0 || check == 4)) {

                                try {

                                    if (readHtml(temp.getAbsolutePath()) > 0) {
                                        set4.add(temp);
                                        Platform.runLater(()->{
                                            displayList.add(temp);
                                        });
                                    }
                                } catch (Exception e) {

                                }

                            }
                            
                        });
                    } catch (Exception e) {
                    }
                    if (threadState2 == false) {
                        files.clear();
                        break;
                    }
                } else if (file.getName().endsWith(ext) && (check == 0 || check == 2)) {
                    try {
                        if (readDocFile(file.getAbsolutePath()) > 0) {
                            set.add(file);
                            Platform.runLater(()->{
                                displayList.add(file);
                            });
                        }
                    } catch (Exception e) {
                    }
                } else if (file.getName().endsWith(ext1) && (check == 0 || check == 1)) {

                    try {
                        if (pdFind(file.getAbsolutePath()) > 0) {
                            set1.add(file);
                            Platform.runLater(()->{
                                displayList.add(file);
                            });
                        }
                    } catch (Exception e) {
                    }
                } else if (file.getName().endsWith(ext2) && (check == 0 || check == 3)) {

                    try {
                        if (chkTxt(word, file.getAbsolutePath()) > 0) {
                            set2.add(file);
                            Platform.runLater(()->{
                                displayList.add(file);
                            });
                        }
                    } catch (Exception e) {

                    }
                } else if (file.getName().endsWith(ext3) && (check == 0 || check == 2)) {

                    try {
                        if (readDocxFile(file.getAbsolutePath()) > 0) {
                            set3.add(file);
                            Platform.runLater(()->{
                                displayList.add(file);
                            });
                        }
                    } catch (Exception e) {
                    }
                } else if (file.getName().endsWith(ext4) && (check == 0 || check == 4)) {

                    try {
                        if (readDocxFile(file.getAbsolutePath()) > 0) {
                            set4.add(file);
                            Platform.runLater(()->{
                                displayList.add(file);
                            });
                        }
                    } catch (Exception e) {
                    }
                }
                if(threadState2 == false){
                    break;
                }

            } catch (IOException iOException) {

            }
            break;
        }
    }
}
    
 
