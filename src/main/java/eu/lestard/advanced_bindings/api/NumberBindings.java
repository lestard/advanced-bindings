/*
 * Copyright (c) 2014-2016 Manuel Mauky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.lestard.advanced_bindings.api;

import javafx.beans.binding.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.*;

public class NumberBindings {

    /**
     * A boolean binding that is `true` if the given observable double is **Not a Number**.
     * See {@link Double#isNaN(double)}.
     *
     * @param observableValue the observable double value to use for the binding.
     * @return the boolean binding.
     */
    public static BooleanBinding isNaN(final ObservableDoubleValue observableValue) {
        return Bindings.createBooleanBinding(() -> Double.isNaN(observableValue.get()), observableValue);
    }

    /**
     * A boolean binding that is `true` if the given observable double is **Infinite**.
     * See {@link Double#isInfinite(double)}.
     *
     * @param observableValue the observable double value to use for the binding.
     * @return the boolean binding.
     */
    public static BooleanBinding isInfinite(final ObservableDoubleValue observableValue) {
        return Bindings.createBooleanBinding(() -> Double.isInfinite(observableValue.get()), observableValue);
    }


    /**
     * An number binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more informations.
     *
     * @param dividend     the observable value used as dividend
     * @param divisor      the observable value used as divisor
     * @return the resulting number binding
     */
    public static NumberBinding divideSafe(ObservableValue<Number> dividend, ObservableValue<Number> divisor) {
        return divideSafe(dividend, divisor, new SimpleDoubleProperty(0));
    }


    /**
     * An number binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more informations.
     *
     * @param dividend     the value used as dividend
     * @param divisor      the observable value used as divisor
     * @return the resulting number binding
     */
    public static NumberBinding divideSafe(double dividend, ObservableValue<Number> divisor) {
        return divideSafe(new SimpleDoubleProperty(dividend), divisor);
    }


    /**
     * An number binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more informations.
     *
     * @param dividend     the observable value used as dividend
     * @param divisor      the value used as divisor
     * @return the resulting number binding
     */
    public static NumberBinding divideSafe(ObservableValue<Number> dividend, double divisor) {
        return divideSafe(dividend, new SimpleDoubleProperty(divisor));
    }

    /**
     * An number binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more informations.
     *
     * @param dividend     the observable value used as dividend
     * @param divisor      the observable value used as divisor
     * @param defaultValue the observable value that is used as default value. The binding will have this value when a
     *                     division by zero happens.
     * @return the resulting number binding
     */
    public static NumberBinding divideSafe(ObservableValue<Number> dividend, ObservableValue<Number> divisor, ObservableValue<Number> defaultValue) {
        return Bindings.createDoubleBinding(() -> {

            if (divisor.getValue().doubleValue() == 0) {
                return defaultValue.getValue().doubleValue();
            } else {
                return dividend.getValue().doubleValue() / divisor.getValue().doubleValue();
            }

        }, dividend, divisor);
    }

    /**
     * An number binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more information.
     *
     * @param dividend     the observable value used as dividend
     * @param divisor      the observable value used as divisor
     * @param defaultValue the default value. The binding will have this value when a
     *                     division by zero happens.
     * @return the resulting number binding
     */
    public static NumberBinding divideSafe(ObservableValue<Number> dividend, ObservableValue<Number> divisor, double defaultValue) {
        return divideSafe(dividend, divisor, new SimpleDoubleProperty(defaultValue));
    }


    /**
     * A number binding with the division of the two observable number values.
     * The difference to the existing bindings (
     * {@link Bindings#divide(javafx.beans.value.ObservableNumberValue, javafx.beans.value.ObservableNumberValue)})
     * is that this binding will **not** throw an {@link java.lang.ArithmeticException} when the second param (the
     * divisor)
     * is zero. Instead an default value of `0` is returned.
     *
     * This can be useful because bindings like this aren't working as expected:
     *
     * ```java
     * IntegerProperty a = ...;
     * IntegerProperty b = ...;
     *
     * NumberBinding result = Bindings
     *      .when(b.isEqualTo(0))
     *      .then(0)
     *      .otherwise(a.divide(b));
     * ```
     *
     * At first one would expect that this binding will have a value `0` when `b` is `0`.
     * Instead the binding in the example will throw an {@link java.lang.ArithmeticException} when `b`
     * has an (initial) value of 0. The when-otherwise construct doesn't help in this case because
     * the `divide` binding will still be evaluated.
     *
     * In such cases you can use the binding created by this method which makes this distinction internally
     * and won't throw an exception.
     *
     *
     * If you `0` isn't suitable as a default value for your use case you can use the overloaded
     * method {@link #divideSafe(javafx.beans.value.ObservableIntegerValue, javafx.beans.value.ObservableIntegerValue,
     * int)}.
     *
     * @param dividend the observable value used as dividend
     * @param divisor  the observable value used as divisor
     * @return the resulting integer binding
     */
    public static IntegerBinding divideSafe(ObservableIntegerValue dividend, ObservableIntegerValue divisor) {
        return divideSafe(dividend, divisor, new SimpleIntegerProperty(0));
    }

    /**
     * An integer binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more information.
     *
     * @param dividend the value used as dividend
     * @param divisor  the observable value used as divisor
     * @return the resulting integer binding
     */
    public static IntegerBinding divideSafe(int dividend, ObservableIntegerValue divisor) {
        return divideSafe(new SimpleIntegerProperty(dividend), divisor);
    }

    /**
     * An integer binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more information.
     *
     * @param dividend the observable value used as dividend
     * @param divisor  the value used as divisor
     * @return the resulting integer binding
     */
    public static IntegerBinding divideSafe(ObservableIntegerValue dividend, int divisor) {
        return divideSafe(dividend, new SimpleIntegerProperty(divisor));
    }


    /**
     * An integer binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more information.
     *
     * @param dividend     the observable value used as dividend
     * @param divisor      the observable value used as divisor
     * @param defaultValue the observable value that is used as default value. The binding will have this value when a
     *                     division by zero happens.
     * @return the resulting integer binding
     */
    public static IntegerBinding divideSafe(ObservableIntegerValue dividend, ObservableIntegerValue divisor, ObservableIntegerValue defaultValue) {
        return Bindings.createIntegerBinding(() -> {

            if (divisor.intValue() == 0) {
                return defaultValue.get();
            } else {
                return dividend.intValue() / divisor.intValue();
            }

        }, dividend, divisor);
    }

    /**
     * An integer binding of a division that won't throw an {@link java.lang.ArithmeticException}
     * when a division by zero happens. See {@link #divideSafe(javafx.beans.value.ObservableIntegerValue,
     * javafx.beans.value.ObservableIntegerValue)}
     * for more information.
     *
     * @param dividend     the observable value used as dividend
     * @param divisor      the observable value used as divisor
     * @param defaultValue the default value. The binding will have this value when a
     *                     division by zero happens.
     * @return the resulting integer binding
     */
    public static IntegerBinding divideSafe(ObservableIntegerValue dividend, ObservableIntegerValue divisor, int defaultValue) {
        return divideSafe(dividend, divisor, new SimpleIntegerProperty(defaultValue));
    }

    /**
     * Converts the given {@link javafx.beans.value.ObservableValue} of type {@link Number}
     * into an {@link javafx.beans.binding.IntegerBinding}.
     *
     * This is done by the {@link Number#intValue()} method.
     *
     * This method can be useful in situations where an operation or component only provides an observable value
     * of type number that you like to use to create calculation bindings.
     *
     * It's the like a reverse-method of {@link javafx.beans.binding.IntegerBinding#asObject()}.
     *
     * @param source the observable value that will be converted.
     * @return the IntegerBinding that holds the value of the source observable.
     */
    public static IntegerBinding asInteger(final ObservableValue<Number> source) {
        return Bindings.createIntegerBinding(()-> nonNullNumber(source.getValue()).intValue(),source);
    }

    /**
     * Converts the given {@link javafx.beans.value.ObservableValue} of type {@link Number}
     * into an {@link javafx.beans.binding.DoubleBinding}.
     *
     * This is done by the {@link Number#doubleValue()} method.
     *
     * This method can be useful in situations where an operation or component only provides an observable value
     * of type number that you like to use to create calculation bindings.
     *
     * It's the like a reverse-method of {@link javafx.beans.binding.DoubleBinding#asObject()}.
     *
     * @param source the observable value that will be converted.
     * @return the DoubleBinding that holds the value of the source observable.
     */
    public static DoubleBinding asDouble(final ObservableValue<Number> source) {
        return Bindings.createDoubleBinding(() -> nonNullNumber(source.getValue()).doubleValue(), source);
    }

    /**
     * Converts the given {@link javafx.beans.value.ObservableValue} of type {@link Number}
     * into an {@link javafx.beans.binding.FloatBinding}.
     *
     * This is done by the {@link Number#floatValue()} method.
     *
     * This method can be useful in situations where an operation or component only provides an observable value
     * of type number that you like to use to create calculation bindings.
     *
     * It's the like a reverse-method of {@link javafx.beans.binding.FloatBinding#asObject()}.
     *
     * @param source the observable value that will be converted.
     * @return the FloatBinding that holds the value of the source observable.
     */
    public static FloatBinding asFloat(final ObservableValue<Number> source) {
        return Bindings.createFloatBinding(() -> nonNullNumber(source.getValue()).floatValue(), source);
    }

    /**
     * Converts the given {@link javafx.beans.value.ObservableValue} of type {@link Number}
     * into an {@link javafx.beans.binding.LongBinding}.
     *
     * This is done by the {@link Number#longValue()} method.
     *
     * This method can be useful in situations where an operation or component only provides an observable value
     * of type number that you like to use to create calculation bindings.
     *
     * It's the like a reverse-method of {@link javafx.beans.binding.LongBinding#asObject()}.
     *
     * @param source the observable value that will be converted.
     * @return the LongBinding that holds the value of the source observable.
     */
    public static LongBinding asLong(final ObservableValue<Number> source) {
        return Bindings.createLongBinding(() -> nonNullNumber(source.getValue()).longValue(), source);
    }


    /**
     * Helper to prevent {@link java.lang.NullPointerException}. When the given number param is <code>null</code>
     * this method returns <code>0</code>. Otherwise the given number is returned.
     */
    private static Number nonNullNumber(Number number){
        if(number == null){
            return 0;
        }else{
            return number;
        }
    }

}
