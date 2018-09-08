package fr.cmdor;

import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.EmojiOneView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.icons525.Icons525;
import de.jensd.fx.glyphs.icons525.Icons525View;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

/**
 * Created on 08/09/2018.
 *
 * @author Sébastien Bourguet
 */
public class ViewerController implements Initializable {
    private final LinkedHashMap<Class<? extends GlyphIcon>, Collection<GlyphIcons>> fonts = new LinkedHashMap<>();
    @FXML
    private Accordion libraries;
    @FXML
    private Slider sizeSlider;
    @FXML
    private Label sizeLabel;
    @FXML
    private SplitMenuButton actionBtn;
    @FXML
    private TextField search;
    @FXML
    private MaterialDesignIconView searchIcon;
    @FXML
    private FlowPane searchResultPane;
    private GaussianBlur blur;
    private GlyphIcons selectedIcon;
    private float iconPrefSize = 24.0F;
    private int flowPrefGap = 12;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        blur = new GaussianBlur();
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.3D);
        this.blur.setInput(colorAdjust);
        this.sizeSlider.valueProperty().addListener((o, old, n) -> {
            this.sizeLabel.setText(String.format("%.3f px.", n.floatValue()));
            this.iconPrefSize = n.floatValue();
            this.libraries.getPanes().forEach(p ->
                    ((FlowPane) ((ScrollPane) p.getContent()).getContent()).getChildren().forEach(i -> {
                        ((GlyphIcon<?>) i).setGlyphSize(this.iconPrefSize);
                    }));
        });

        this.actionBtn.setOnMouseClicked(e -> {
            if (this.selectedIcon != null) {
                ClipboardContent cc = new ClipboardContent();
                cc.putString(selectedIcon.name());
                Clipboard.getSystemClipboard().setContent(cc);
            }
        });

        this.search.textProperty().addListener((o, old, n) -> {
            if (n.equals(old)) return;
            this.searchResultPane.getParent().getParent().getParent().setVisible(!n.isEmpty());
            this.libraries.setEffect(n.isEmpty() ? null : this.blur);
            this.searchIcon.setGlyphName(
                    n.isEmpty() ? MaterialDesignIcon.MAGNIFY.name() : MaterialDesignIcon.CLOSE_CIRCLE.name());
            this.search(n);
        });

        this.search.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) this.search.setText("");
        });
        this.searchIcon.setOnMouseClicked(e -> {
            if (this.searchIcon.getGlyphName().equals(MaterialDesignIcon.CLOSE_CIRCLE.name())) {
                this.search.setText("");
            }
        });

        this.generateFontsPane();
    }

    @FXML
    private void changeAction(ActionEvent e) {
        // TODO
    }

    private void generateFontsPane() {
        fonts.put(EmojiOneView.class, Arrays.asList(EmojiOne.values()));
        this.createFontView(EmojiOne.values(), EmojiOneView.class, "EmojiOne");
        fonts.put(FontAwesomeIconView.class, Arrays.asList(FontAwesomeIcon.values()));
        this.createFontView(FontAwesomeIcon.values(), FontAwesomeIconView.class, "FontAwesome");
        fonts.put(Icons525View.class, Arrays.asList(Icons525.values()));
        this.createFontView(Icons525.values(), Icons525View.class, "Icon 525");
        fonts.put(MaterialDesignIconView.class, Arrays.asList(MaterialDesignIcon.values()));
        this.createFontView(MaterialDesignIcon.values(), MaterialDesignIconView.class, "Material Design");
        fonts.put(MaterialIconView.class, Arrays.asList(MaterialIcon.values()));
        this.createFontView(MaterialIcon.values(), MaterialIconView.class, "Material");
        fonts.put(OctIconView.class, Arrays.asList(OctIcon.values()));
        this.createFontView(OctIcon.values(), OctIconView.class, "Oct");
        fonts.put(WeatherIconView.class, Arrays.asList(WeatherIcon.values()));
        this.createFontView(WeatherIcon.values(), WeatherIconView.class, "Weather");
    }

    private <E extends Enum<E> & GlyphIcons> void createFontView(E[] e, Class<? extends GlyphIcon> v, String name) {
        FlowPane p = new FlowPane();
        p.setHgap(this.flowPrefGap);
        p.setVgap(this.flowPrefGap);
        Arrays.asList(e).forEach(i -> {
            p.getChildren().add(this.createIconView(i, v));
        });
        ScrollPane s = new ScrollPane(p);
        s.setFitToHeight(true);
        s.setFitToWidth(true);
        this.libraries.getPanes().add(new TitledPane(name, s));
    }

    private GlyphIcon<?> createIconView(GlyphIcons i, Class<? extends GlyphIcon> v) {
        GlyphIcon<?> viewer;
        try {
            viewer = v.newInstance();
            viewer.setGlyphName(i.name());
        } catch (InstantiationException | IllegalAccessException e) {
            viewer = new FontAwesomeIconView(FontAwesomeIcon.QUESTION_CIRCLE);
        }
        viewer.setGlyphSize(this.iconPrefSize);
        viewer.setOnMouseClicked(e -> {
            this.selectedIcon = i;
            this.actionBtn.setText(i.name());
        });
        Tooltip.install(viewer, new Tooltip(i.name()));
        return viewer;
    }

    private void search(final String text) {
        if (text == null || text.isEmpty()) return;
        final String searchText = text.toLowerCase();
        this.searchResultPane.getChildren().clear();
        this.fonts.forEach((v, icons) -> {
            icons.forEach(i -> {
                if (i.name().toLowerCase().contains(searchText)) {
                    this.searchResultPane.getChildren().add(this.createIconView(i, v));
                }
            });
        });
    }
}
