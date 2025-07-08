package ludo.mentis.aciem.scl.dev;

public interface DataLoaderCommand {
	
	int getOrder();
	String getName();
	boolean canItRun();
	int run();
}
