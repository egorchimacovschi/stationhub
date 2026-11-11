package  com.stationhub;

import com.stationhub.app.Engine;
import java.nio.file.Path;

public final class Main {
    
    private Main() {}

    public static void main(String[] args){
        
        if(args.length != 2){
            System.err.println("Usage: java -jar stationhub.jar <input.json> <output.json>");
            System.exit(1);
        }

        new Engine().run(Path.of(args[0]), Path.of(args[1]));
    }
}
