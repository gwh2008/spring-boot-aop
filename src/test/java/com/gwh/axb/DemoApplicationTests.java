package com.gwh.axb;

import com.gwh.axb.controller.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DemoApplicationTests {

	@Resource
    private TestController controller;


	@Test
	void contextLoads() {
	}

	    @Test
    public void bind() throws Exception{
//        String[] a_ = new String[]{"18610265322","18810682789","13301276530"}; //"18901339300";
//        String[] x_ = new String[]{"18002005524","18002003946","18002003947"};
//        for(int i=0; i<a_.length; i++)
//        {
//            String a = a_[i];
//            String x = x_[i];
//            controller.bind(a, x);
//        }
        String a = "13301276530";
        String x = "18116260864";
        String b = "13301276530";
        controller.bind("1", "","", a ,b,"1","10");

    }

}
