package com.phi.generator;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * Test form generation
 * Created by Alex on 18/11/2016.
 */
public class PhiGeneratorMojoTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGenerateForm() throws Exception {
        File pom = getTestFile("src/test/resources/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        PhiGeneratorMojo myMojo = (PhiGeneratorMojo) lookupMojo("generate", pom);
        assertNotNull(myMojo);
        myMojo.execute();
    }
}
