package user_interface;

import functional.DataSaveLoad;
import functional.UserData;
import functional.XmlFileWorking;
import lang.Strings_EN;
import lang.Strings_RU;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import java.io.*;
import java.text.ParseException;

public class MainMenu {
    protected static PrintWriter out = new PrintWriter(System.out, true);
    protected static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    protected static DataSaveLoad xmlFiles = new XmlFileWorking();


    public static void init() {
        try {
            UserData.users = new XmlFileWorking().loadUsers(DataSaveLoad.XML_USERS);
            System.out.println(UserData.users.get(0).getLogin());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        runServerInit();

        chooseLocale();

    }

    private static void chooseLocale() {
        int N = 1024;
        UserData.rsa.init(N);
        int choice;
        while (true) {
            out.println(Resources.language.getSTART_CHOICE());
            try {
                choice = Integer.parseInt(reader.readLine());
                switch (choice) {
                    case 1:
                        Resources.language = new Strings_RU();
                        readArrays();
                        if (UserData.currentUser == null) {
                            UserHandler.logIn();
                        } else {
                            mainMenu();
                        }
                        break;
                    case 2:
                        Resources.language = new Strings_EN();
                        readArrays();
                        if (UserData.currentUser == null) {
                            UserHandler.logIn();
                        } else {
                            mainMenu();
                        }
                        break;
                    case 3:
                        new XmlFileWorking().saveUsers(UserData.users,DataSaveLoad.XML_USERS);
                        System.exit(0);
                        break;
                    default:
                        out.println(Resources.language.getWRONG_CHOICE());
                }
            } catch (NumberFormatException ex) {
                out.println(Resources.language.getWRONG_CHOICE());
            } catch (IOException ex) {
                out.println(Resources.language.getIO_ERROR());
            }
        }
    }

    protected static void mainMenu() {
        if (UserData.currentUser != null) {
            out.println(Resources.language.getMAIN_MENU());
        } else {
            out.println(Resources.language.getGUEST_MAIN_MENU());
        }
        int choice;
        boolean guest_flag = false;
        try {
            choice = Integer.parseInt(reader.readLine());
            switch (choice) {
                case 1:
                    if (UserData.currentUser != null) {
                        AddHandler.addMenu();
                        break;
                    } else {
                        guest_flag = true;
                    }
                case 2:
                    if (UserData.currentUser != null || guest_flag) {
                        SearchHandler.searchMenu();
                        break;
                    } else {
                        guest_flag = true;
                    }
                case 3:
                    if (UserData.currentUser != null || guest_flag) {
                        PrintHandler.showMenu();
                        break;
                    } else {
                        guest_flag = true;
                    }
                case 4:
                    if (UserData.currentUser != null || guest_flag) {
                        if (Resources.language.getClass() == Strings_EN.class) help("./resources/helps/help_en.txt");
                        else help("./resources/helps/help_ru.txt");
                        break;
                    } else {
                        guest_flag = true;
                    }
                case 5:
                    if (UserData.currentUser != null || guest_flag) {
                        chooseLocale();
                        break;
                    }
                case 6:
                    if (UserData.currentUser != null) {
                        UserData.logOut(Resources.traditions, Resources.countries, Resources.holidays);
                        UserHandler.logIn();
                        break;
                    }
                case 7:
                    exit();
                    break;
                default:
                    out.println(Resources.language.getWRONG_CHOICE());
                    mainMenu();
                    break;
            }
        } catch (NumberFormatException ex) {
            out.println(Resources.language.getWRONG_CHOICE());
            mainMenu();
        } catch (IOException e) {
            out.println(Resources.language.getIO_ERROR());
            mainMenu();
        }
    }

    private static void help(String path) {
        File helpFile = new File(path);
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(helpFile));
            while (fileReader.ready()) out.println(fileReader.readLine());
            out.println(Resources.language.getHELP_MENU());
            int choice = Integer.parseInt(reader.readLine());
            switch (choice) {
                case 1:
                    mainMenu();
                    break;
                case 2:
                    exit();
                    break;
                default:
                    out.println(Resources.language.getWRONG_CHOICE());
                    mainMenu();
            }
        } catch (FileNotFoundException e) {
            out.println(Resources.language.getHELP_FILE_ERROR());
            mainMenu();
        } catch (IOException e) {
            out.println(Resources.language.getIO_ERROR());
            mainMenu();
        } catch (NumberFormatException ex) {
            out.println(Resources.language.getWRONG_CHOICE());
            mainMenu();
        }
    }

    protected static void exit() { //Куча Исключений, нужны try и catch
        try {
            if (UserData.currentUser != null) {
                UserData.logOut(Resources.traditions, Resources.countries, Resources.holidays);
            }
            writeArrays();
            reader.close();
            out.close();
            System.exit(0);
        } catch (IOException e) { // прописать сюда ошибкииииииии
        } catch (JDOMException e) {
        } catch (ParseException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    private static void readArrays() throws IOException {
        try {
            xmlFiles.loadAll(Resources.traditions, Resources.countries, Resources.holidays);

        } catch (ClassNotFoundException ex) {
            MainMenu.out.println(Resources.language.getNO_CLASS());
        } catch (JDOMException e) {
            MainMenu.out.println(Resources.language.getXML_ERROR());
        } catch (ParseException e) {
            MainMenu.out.println(Resources.language.getPARSE_ERROR());
        } catch (SAXException e) {
            MainMenu.out.println(Resources.language.getXML_ERROR());
        }
    }

    private static void writeArrays() throws IOException, JDOMException, ParseException, ClassNotFoundException {
        xmlFiles.saveAll(Resources.traditions, Resources.countries, Resources.holidays);
          }

    private static void runServerInit(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    myServer.serverInit();
                } catch (IOException e) {

                }

            }
        }).start();
    }

//endregion
}
