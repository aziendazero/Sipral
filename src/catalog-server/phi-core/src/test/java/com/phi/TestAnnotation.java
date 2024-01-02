package com.phi;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;

public class TestAnnotation {
	public static Set<Class<? extends BaseEntity>> classes = new HashSet<Class<? extends BaseEntity>>();
	public static Set<Class<? extends CodeValue>> classesCodeValue = new HashSet<Class<? extends CodeValue>>();
	public static void main(String[] args){
		

		 ComponentScanner scanner = new ComponentScanner();
	        scanner.getClasses(new ComponentQuery() {
	            @Override
	            protected void query() {
	                select().
	                        from("com.phi.entities").
			        andStore(thoseExtending(BaseEntity.class).into(classes)).
			        returning(none());
	            }
	        });
	        scanner.getClasses(new ComponentQuery() {
	            @Override
	            protected void query() {
	                select().
	                        from("com.phi.entities").
			        andStore(thoseExtending(CodeValue.class).into(classesCodeValue)).
			        returning(none());
	            }
	        });
	        StringBuffer buf= new StringBuffer();
	        printClassName(buf,classes);
	        printClassName(buf,classesCodeValue);
	        System.out.println(buf.toString());
	}
	private static void printClassName(StringBuffer buf,Set classesList) {
		for (Object myClass: classesList){
			Class realClass=(Class)myClass;
			if(realClass.getPackage()!=null && realClass.getPackage().getName()!=null){
//	        		System.out.println(myClass.getPackage().getName());
				buf.append("<value>").append(realClass.getName()).append("</value>").append("\n");
			}
		}
	}
}
