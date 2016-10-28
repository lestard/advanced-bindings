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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

public class ObjectBindings {


    /**
     * A Binding that holds a value that is determined by the given function applied to the value of the observable
     * source.
     *
     *
     * This binding is null safe: When the given observable has a value of `null` the created binding
     * will also contain `null` but will **not** throw a {@link java.lang.NullPointerException}.
     *
     * (Hint: if you like to specify another default value look at {@link #map(javafx.beans.value.ObservableValue, java.util.function.Function, Object)}).
     *
     * If the given observable's value is **not** `null` the function will be applied to the value and the
     * return value of the function is used as the value of the created binding.
     *
     * A common use case for this binding is when you are interested in a property of the object of an observable value.
     * See the following example:
     *
     * ```java
     * class Person {
     *     private String name;
     *     ...
     *     public String getName(){
     *         return name;
     *     }
     * }
     *
     * ObjectProperty<Person> personProperty = ...
     *
     * ObjectBinding<String> name = ObjectBindings.map(personProperty, Person::getName);
     * ```
     *
     * The "name" binding in the example always contains the name of the Person that the "personProperty" is holding.
     * As you can see this is a good use case for method references introduced by Java 8.
     *
     *
     * The binding from the example will **not** throw a @see java.lang.NullPointerException even if the personProperty
     * holds `null` as value:
     *
     * ```java
     * personProperty.set(null);
     *
     * assertThat(name.get()).isNull();
     * ```
     *
     * Your mapping function will only be called when the observable value is not null.
     * This means that you don't need to check for `null` param
     * in you mapping function.
     *
     * @param source the observable value that is the source for this binding.
     * @param function a function that maps the value of the source to the target binding.
     * @param <S> the generic type of the source observable.
     * @param <R> the generic type of the resulting binding.
     * @return the created binding.
     */
    public static <S, R> ObjectBinding<R> map(ObservableValue<S> source, Function<? super S, ? extends R> function) {
        return map(source, function, null);
    }

    /**
     * A null safe binding that holds the return value of the given function when applied to the value of the given observable.
     *
     * If the observable has a value of `null` the given default value (third param) will be used as value for the binding instead.
     *
     * The given function will never get `null` as param so you don't need to check for this.
     *
     * See {@link #map(javafx.beans.value.ObservableValue, java.util.function.Function, Object)} for a detailed explanation of
     * the binding.
     *
     * @param source the observable value that is the source for this binding.
     * @param function a function that maps the value of the source to the target binding.
     * @param defaultValue the default value that is used when the source observable has a value of `null`.
     * @param <S> the generic type of the source observable.
     * @param <R> the generic type of the resulting binding.
     * @return the created binding.
     */
    public static <S, R> ObjectBinding<R> map(ObservableValue<S> source, Function<? super S, ? extends R> function, R defaultValue){
        return Bindings.createObjectBinding(()->{
            S sourceValue = source.getValue();

            if(sourceValue == null){
                return defaultValue;
            }else{
                return function.apply(sourceValue);
            }
        }, source);
    }

    /**
     * Creates a binding that contains the same value as the source observable
     * but casted to a type that is higher in class hierarchy.
     *
     * Example:
     *
     * ```java
     * public class Person {
     * }
     *
     * public class Student extends Person {
     * }
     *
     *
     * ObjectProperty<Student> student = new SimpleObjectProperty<>();
     *
     * ObjectBinding<Person> person = ObjectBindings.cast(student);
     * ```
     *
     * This can be useful for cases when a method needs an observable value of the base type as argument
     * but you have only an observable of a more specific type.
     * The reason is that there is no implicit type conversion.
     *
     * See another example:
     *
     * ```java
     * public void calculate(ObservableValue<Number> value) {
     *     ...
     * }
     *
     *  ...
     *
     * ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
     * choiceBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
     *
     * ObjectProperty<Integer> value = choiceBox.valueProperty();
     *
     * calculate(value) // compile error
     *
     * calculate(ObjectBindings.cast(value)); // this works
     * ```
     *
     * While `Integer` can be implicitly casted to `Number` the same isn't possible
     * from `ObservableValue<Integer>` to `ObservableValue<Number>`.
     * For such use cases you can use this method.
     *
     * @param source the observable value that will be used as source.
     * @param <T> the generic type of target binding.
     * @param <S> the generic type of the source binding.
     * @return an ObjectBinding that will contain the same value of the source but casted to another type.
     */
    public static <T, S extends T> ObjectBinding<T> cast(final ObservableValue<S> source) {
        return Bindings.createObjectBinding(source::getValue, source);
    }
}
