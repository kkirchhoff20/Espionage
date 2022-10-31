module EFX {
	requires javafx.controls;
	requires javafx.fxml;
	requires jnativehook;
	requires slf4j.api;
	requires java.logging;
	requires java.desktop;
	requires org.jsoup;
	requires javafx.graphics;
	
	
	opens application to javafx.graphics, javafx.fxml;
}
