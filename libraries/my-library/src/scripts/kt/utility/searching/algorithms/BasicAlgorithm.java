package scripts.kt.utility.searching.algorithms;

import scripts.kt.utility.searching.Applicable;
import scripts.kt.utility.searching.ToStringFunction;

public abstract class BasicAlgorithm implements Applicable {

    private ToStringFunction<String> stringFunction;

    public BasicAlgorithm() {
        this.stringFunction = new DefaultStringFunction();
    }

    public BasicAlgorithm(ToStringFunction<String> stringFunction) {
        this.stringFunction = stringFunction;
    }

    public abstract int apply(String s1, String s2, ToStringFunction<String> stringProcessor);

    public int apply(String s1, String s2){

        return apply(s1, s2, this.stringFunction);

    }

    public BasicAlgorithm with(ToStringFunction<String> stringFunction){
        setStringFunction(stringFunction);
        return this;
    }

    public BasicAlgorithm noProcessor(){
        this.stringFunction = ToStringFunction.NO_PROCESS;
        return this;
    }

    void setStringFunction(ToStringFunction<String> stringFunction){
        this.stringFunction = stringFunction;
    }

    public ToStringFunction<String> getStringFunction() {
        return stringFunction;
    }
}
