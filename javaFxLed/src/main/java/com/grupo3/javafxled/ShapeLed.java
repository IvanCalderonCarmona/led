/*
 * Copyright (c) 2013. by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grupo3.javafxled;


import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/**
 * Created by User: hansolo Date: 05.04.13 Time: 14:39
 */
public class ShapeLed extends Region {

    private static final double PREFERRED_SIZE = 16;
    private static final double MINIMUM_SIZE = 8;
    private static final double MAXIMUM_SIZE = 1024;
    private static final long SHORTEST_INTERVAL = 50_000_000l;
    private static final long LONGEST_INTERVAL = 5_000_000_000l;

    private ObjectProperty<Color> ledColor;
    private BooleanProperty on;
    private boolean _blinking = false;
    private BooleanProperty blinking;
    private boolean _frameVisible = true;
    private BooleanProperty frameVisible;
    private InnerShadow ledOnShadow;
    private InnerShadow ledOffShadow;
    private LinearGradient frameGradient;
    private LinearGradient ledOnGradient;
    private LinearGradient ledOffGradient;
    private RadialGradient highlightGradient;
    // Linear para usar en el rectangulo
    private LinearGradient linear;
    private long lastTimerCall;
    private long _interval = 500_000_000l;
    private LongProperty interval;
    private final AnimationTimer timer;
    // Para guardar el color en la variable que se utiliza en el ejercicio 2
    private Color aux;
    // Propiedad borderColor
    private ObjectProperty<Color> borderColor;

    // ******************** Constructors **************************************
    public ShapeLed() {
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(final long NOW) {
                if (NOW > lastTimerCall + getInterval()) {
                    setOn(!isOn());
                    lastTimerCall = NOW;
                }
            }
        };
        init();
        initGraphics();
        registerListeners();

    }

    // ******************** Initialization ************************************
    private void init() {
        if (getWidth() <= 0 || getHeight() <= 0
                || getPrefWidth() <= 0 || getPrefHeight() <= 0) {
            setPrefSize(PREFERRED_SIZE, PREFERRED_SIZE);
        }
        if (getMinWidth() <= 0 || getMinHeight() <= 0) {
            setMinSize(MINIMUM_SIZE, MINIMUM_SIZE);
        }
        if (getMaxWidth() <= 0 || getMaxHeight() <= 0) {
            setMaxSize(MAXIMUM_SIZE, MAXIMUM_SIZE);
        }
    }

    private void initGraphics() {
//        canvas = new Canvas();
//        ctx    = canvas.getGraphicsContext2D();
//        getChildren().add(canvas);
    }

    // Cuando cambian las propiedades, hay que redibujar
    private void registerListeners() {
        widthProperty().addListener(observable -> recalc());
        heightProperty().addListener(observable -> recalc());
        //frameVisibleProperty().addListener(observable -> draw());
        //onProperty().addListener(observable -> draw());
        
        //********** Rectangulo Ejercicio 5 puedes probarlo ********************
        frameVisibleProperty().addListener(observable -> drawRectangule());
        onProperty().addListener(observable -> drawRectangule());
        
        
        ledColorProperty().addListener(observable -> recalc());
        borderColorProperty().addListener(observable -> recalc());

        //************** Ejercicio 1 ******************************************
        this.setOnMouseClicked((MouseEvent event) -> {

            if (this.isBlinking()) {
                this.setBlinking(false);

            } else {
                this.setBlinking(true);
            }

        });
        //************** Ejercicio 2 ******************************************
        this.setOnMouseEntered((MouseEvent event) -> {
            aux = this.getLedColor();
            this.setLedColor(Color.PINK);

        });

        this.setOnMouseExited((MouseEvent event) -> {
            this.setLedColor(aux);

        });

    }

    // ******************** Methods *******************************************
    public final boolean isOn() {
        return null == on ? false : on.get();
    }

    public final void setOn(final boolean ON) {
        onProperty().set(ON);
    }

    public final BooleanProperty onProperty() {
        if (null == on) {
            on = new SimpleBooleanProperty(this, "on", false);
        }
        return on;
    }

    public final boolean isBlinking() {
        return null == blinking ? _blinking : blinking.get();
    }

    public final void setBlinking(final boolean BLINKING) {
        if (null == blinking) {
            _blinking = BLINKING;
            if (BLINKING) {
                timer.start();
            } else {
                timer.stop();
                setOn(false);
            }
        } else {
            blinking.set(BLINKING);
        }
    }

    public final BooleanProperty blinkingProperty() {
        if (null == blinking) {
            blinking = new BooleanPropertyBase() {
                @Override
                public void set(final boolean BLINKING) {
                    super.set(BLINKING);
                    if (BLINKING) {
                        timer.start();
                    } else {
                        timer.stop();
                        setOn(false);
                    }
                }

                @Override
                public Object getBean() {
                    return ShapeLed.this;
                }

                @Override
                public String getName() {
                    return "blinking";
                }
            };
        }
        return blinking;
    }

    public final long getInterval() {
        return null == interval ? _interval : interval.get();
    }

    public final void setInterval(final long INTERVAL) {
        if (null == interval) {
            _interval = clamp(SHORTEST_INTERVAL, LONGEST_INTERVAL, INTERVAL);
        } else {
            interval.set(INTERVAL);
        }
    }

    public final LongProperty intervalProperty() {
        if (null == interval) {
            interval = new LongPropertyBase() {
                @Override
                public void set(final long INTERVAL) {
                    super.set(clamp(SHORTEST_INTERVAL, LONGEST_INTERVAL, INTERVAL));
                }

                @Override
                public Object getBean() {
                    return ShapeLed.this;
                }

                @Override
                public String getName() {
                    return "interval";
                }
            };
        }
        return interval;
    }

    public final boolean isFrameVisible() {
        return null == frameVisible ? _frameVisible : frameVisible.get();
    }

    public final void setFrameVisible(final boolean FRAME_VISIBLE) {
        if (null == frameVisible) {
            _frameVisible = FRAME_VISIBLE;
        } else {
            frameVisible.set(FRAME_VISIBLE);
        }
    }

    public final BooleanProperty frameVisibleProperty() {
        if (null == frameVisible) {
            frameVisible = new SimpleBooleanProperty(this, "frameVisible", _frameVisible);
        }
        return frameVisible;
    }

    //************************** Ejercicio 3 *************************************************
    //********************** getter and setter boderColor************************************
    public final Color getBorderColor() {
        return null == borderColor ? Color.RED : borderColor.get();
    }

    public final void setBorderColor(final Color borderColor) {
        borderColorProperty().set(borderColor);
    }

    public final ObjectProperty<Color> borderColorProperty() {
        if (null == borderColor) {
            borderColor = new SimpleObjectProperty<>(this, "borderColor", getBorderColor());
        }
        return borderColor;
    }

    //****************************************************************************************** 
    public final Color getLedColor() {
        return null == ledColor ? Color.RED : ledColor.get();
    }

    public final void setLedColor(final Color LED_COLOR) {
        ledColorProperty().set(LED_COLOR);
    }

    public final ObjectProperty<Color> ledColorProperty() {
        if (null == ledColor) {
            ledColor = new SimpleObjectProperty<>(this, "ledColor", Color.RED);
        }
        return ledColor;
    }

    // ******************** Utility Methods ***********************************
    public static long clamp(final long MIN, final long MAX, final long VALUE) {
        if (VALUE < MIN) {
            return MIN;
        }
        if (VALUE > MAX) {
            return MAX;
        }
        return VALUE;
    }

    // ******************** Resize/Redraw *************************************
    private void recalc() {
        double size = getWidth() < getHeight() ? getWidth() : getHeight();

        ledOffShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0.07 * size, 0, 0, 0);

        ledOnShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0.07 * size, 0, 0, 0);
        ledOnShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, ledColor.get(), 0.36 * size, 0, 0, 0));

        frameGradient = new LinearGradient(0.14 * size, 0.14 * size,
                0.84 * size, 0.84 * size,
                false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(20, 20, 20, 0.65)),
                new Stop(0.15, Color.rgb(20, 20, 20, 0.65)),
                new Stop(0.26, Color.rgb(41, 41, 41, 0.65)),
                new Stop(0.26, Color.rgb(41, 41, 41, 0.64)),
                new Stop(0.85, getBorderColor()),
                new Stop(1.0, getBorderColor()));

        ledOnGradient = new LinearGradient(0.25 * size, 0.25 * size,
                0.74 * size, 0.74 * size,
                false, CycleMethod.NO_CYCLE,
                new Stop(0.0, ledColor.get().deriveColor(0d, 1d, 0.77, 1d)),
                new Stop(0.49, ledColor.get().deriveColor(0d, 1d, 0.5, 1d)),
                new Stop(1.0, ledColor.get()));

        ledOffGradient = new LinearGradient(0.25 * size, 0.25 * size,
                0.74 * size, 0.74 * size,
                false, CycleMethod.NO_CYCLE,
                new Stop(0.0, ledColor.get().deriveColor(0d, 1d, 0.20, 1d)),
                new Stop(0.49, ledColor.get().deriveColor(0d, 1d, 0.13, 1d)),
                new Stop(1.0, ledColor.get().deriveColor(0d, 1d, 0.2, 1d)));

        highlightGradient = new RadialGradient(0, 0,
                0.3 * size, 0.3 * size,
                0.29 * size,
                false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.WHITE),
                new Stop(1.0, Color.TRANSPARENT));

        //********* Para el led rectangulo, tienes queser un LinearGradient****
        linear = new LinearGradient(0, 0,
                0.3 * size, 0.3 * size,
                false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.WHITE),
                new Stop(1.0, Color.TRANSPARENT));

        //draw();
        //*** rectangulo  ejercicio 5 ********
        drawRectangule();
    }

    private void draw() {

        double width = getWidth();
        double height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        double centerX = width / 2;
        double centerY = height / 2;

        double size = width < height ? width / 2 : height / 2;

        // Limpia la región y comienza a dibujar de nuevo
        getChildren().clear();

        if (isFrameVisible()) {
            var oval1 = new Ellipse(centerX, centerY, size, size);
            oval1.setFill(frameGradient);
            getChildren().add(oval1);
        }

        var oval2 = new Ellipse(centerX, centerY, 0.72 * size, 0.72 * size);
        if (isOn()) {
            oval2.setEffect(ledOnShadow);
            oval2.setFill(ledOnGradient);
        } else {
            oval2.setEffect(ledOffShadow);
            oval2.setFill(ledOffGradient);
        }
        getChildren().add(oval2);

        var oval3 = new Ellipse(centerX, centerY, 0.58 * size, 0.58 * size);
        oval3.setFill(highlightGradient);
        getChildren().add(oval3);

    }

    //********************** Ejercicio 5 **************************************
    private void drawRectangule() {

        double width = getWidth();
        double height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        double centerX = width / 2;
        double centerY = height / 2;

        double size = width < height ? width / 2 : height / 2;

        // Limpia la región y comienza a dibujar de nuevo
        getChildren().clear();

        if (isFrameVisible()) {
                                     
            var rect1 = new Rectangle(centerX-size /2, centerY-size /2, size, size);
            rect1.setFill(frameGradient);
            getChildren().add(rect1);

        }

        var rect2 = new Rectangle(centerX-size * 0.72 /2 , centerY-size * 0.72 /2, 0.72 * size, 0.72 * size);
        if (isOn()) {
            rect2.setEffect(ledOnShadow);
            rect2.setFill(ledOnGradient);

        } else {
            rect2.setEffect(ledOffShadow);
            rect2.setFill(ledOffGradient);
        }
        getChildren().add(rect2);

        var rect3 = new Rectangle(centerX-size * 0.72 /2, centerY-size * 0.72 /2, 0.58 * size, 0.58 * size);
        rect3.setFill(linear);
        getChildren().add(rect3);

    }
}
