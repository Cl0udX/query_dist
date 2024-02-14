import com.zeroc.Ice.*;
import FunctionsPoint.*;
import obse.*;

public class Client {
    public static void main(String[] args) {
        try(Communicator communicator = Util.initialize(args, "properties.cfg")){
            ObjectPrx baseSubject = communicator.propertyToProxy("subject.proxy");
            SubjectPrx subject = SubjectPrx.checkedCast(baseSubject);

            ObjectAdapter adapter = communicator.createObjectAdapter("observer");
            ObserverI observer = new ObserverI(subject);
            ObjectPrx obsPrx = adapter.add(observer, Util.stringToIdentity("notNecessary"));
            adapter.activate();
            ObserverPrx observerPrx = ObserverPrx.checkedCast(obsPrx);

            if(subject == null){
                throw new Error("Invalid proxy");
            }
            subject.addObserver(observerPrx);

            communicator.waitForShutdown();
        }
    }
}
