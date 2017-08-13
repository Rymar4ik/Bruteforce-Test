import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * @author Stanislav Rymar
 */
public class PasswordGenerator {

    // I desire don't use lazy-singleton in this realization. Because instance of PasswordGenerator created
    //before threads start, its give me opportunity don't use double-check synchronized getInstance() method.
    private static final PasswordGenerator instance = new PasswordGenerator();

    private static int password;
    private LinkedHashSet<String> recheckPassList;

    private PasswordGenerator() {
        password = 0;
        recheckPassList = new LinkedHashSet<String>();
    }

    public static PasswordGenerator getInstance() {
        return instance;
    }

    public synchronized String getNewPassword() {
        return String.valueOf(password++);
    }

    public boolean isRecheckPassListEmpty() {
        return recheckPassList.size() == 0;
    }


    //get pass from list and delete them after that
    public synchronized String getPassToRecheck() {
        if (!isRecheckPassListEmpty()) {
            Iterator<String> iter = recheckPassList.iterator();
            String result = iter.next();
            iter.remove();
            return result;
        } else
            return null;
    }

    //add password to recheck if something going wrong
    public void addPassToRecheck(String badPassword) {
        recheckPassList.add(badPassword);
    }

}
