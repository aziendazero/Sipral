package elParser;

import com.phi.generator.model.BaseComponent;
import junit.framework.TestCase;

import java.util.List;

/**
 * Test javaEl parsing
 * Created by Alex on 17/02/2017.
 */
public class ElTest extends TestCase{

    public void test1() {
        //String javaEl = "Sessions[3] == function.getHostName()";
        String javaEl = "!empty Procpratiche and Param['p.home.procpratiche.gestioneAttivita'].visible and ProcpraticheAction.temporary.get('color').equals('red') ";
        test(javaEl);
    }

    public void test2() {
        String javaEl = "Sessions[3] == function.getHostName()";
        test(javaEl);
    }

    public void test3() {
        String javaEl = "functions.hasCodeIn(ProcpraticheAction.equal['serviceDeliveryLocation.area'].code,'WORKDISEASE')";
        test(javaEl);
    }

    public void test4() {
        String javaEl = "CodeValueParameterAction.temporary['byRole'][Parameter.id].type.equals('A')";
        test(javaEl);
    }



    private void test(String el) {
        try {
            System.out.println(el);

            List<String> variables = BaseComponent.parseElVariables(el);

            System.out.println(variables);
        } catch (Exception e) {
            System.err.println(el);
            e.printStackTrace();
        }
    }

}