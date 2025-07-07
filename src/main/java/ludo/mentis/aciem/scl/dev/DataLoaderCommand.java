package ludo.mentis.aciem.scl.dev;

public interface DataLoaderCommand {
	
	String getName();
	boolean canItRun();
	int run();
}
