package org.apache.oozie.action.hadoop;

import static junit.framework.Assert.assertEquals;

import org.apache.hadoop.conf.Configuration;
import org.apache.oozie.WorkflowActionBean;
import org.apache.oozie.WorkflowJobBean;
import org.apache.oozie.action.ActionExecutorException;
import org.apache.oozie.service.WorkflowAppService;
import org.apache.oozie.util.XConfiguration;
import org.apache.oozie.util.XmlUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

public class TestSparkActionExecutor extends ActionExecutorTestCase {

    @Test
    public void testSparkOpts() throws Exception {
        String actionXml
                = "<spark xmlns=\"uri:oozie:spark-action:0.2\">"
                + "<job-tracker>" + getJobTrackerUri() + "</job-tracker>"
                + "<name-node>" + getNameNodeUri() + "</name-node>"
                + "<master>MASTER</master>"
                + "<mode>MODE</mode>"
                + "<name>NAME</name>"
                + "<class>MAIN-CLASS</class>"
                + "<jar>JAR</jar>"
                + "<spark-opts>--executor-memory --conf prop1=value1 --conf prop2=value2</spark-opts>"
                + "<arg>A1</arg>" + "<arg>A2</arg>"
                + "</spark>";

        Configuration conf = evaluateSetupActionConf(actionXml);

        assertEquals("--executor-memory --conf prop1=value1 --conf prop2=value2", conf.get(SparkActionExecutor.SPARK_OPTS));
    }

    @Test
    public void testSparkOpt() throws Exception {
        String actionXml
                = "<spark xmlns=\"uri:oozie:spark-action:0.2\">"
                + "<job-tracker>" + getJobTrackerUri() + "</job-tracker>"
                + "<name-node>" + getNameNodeUri() + "</name-node>"
                + "<master>MASTER</master>"
                + "<mode>MODE</mode>"
                + "<name>NAME</name>"
                + "<class>MAIN-CLASS</class>"
                + "<jar>JAR</jar>"
                + "<spark-opt>--executor-memory</spark-opt>"
                + "<spark-opt>--conf prop1=value1</spark-opt>"
                + "<spark-opt>--conf prop2=value2</spark-opt>"
                + "<arg>A1</arg>" + "<arg>A2</arg>"
                + "</spark>";

        Configuration conf = evaluateSetupActionConf(actionXml);

        assertEquals("--executor-memory --conf prop1=value1 --conf prop2=value2", conf.get(SparkActionExecutor.SPARK_OPTS));
    }
	
	private Configuration evaluateSetupActionConf(String actionXml) throws JDOMException, ActionExecutorException, Exception {
		SparkActionExecutor ae = new SparkActionExecutor();
		
		XConfiguration protoConf = new XConfiguration();
		protoConf.set(WorkflowAppService.HADOOP_USER, getTestUser());
		
		WorkflowJobBean wf = createBaseWorkflow(protoConf, "mr-action");
		WorkflowActionBean action = (WorkflowActionBean) wf.getActions().get(0);
		action.setType(ae.getType());
		
		Context context = new Context(wf, action);
		Element eActionXml = XmlUtils.parseXml(actionXml);
		
		Configuration actionConf = ae.createBaseHadoopConf(context, eActionXml);
		return ae.setupActionConf(actionConf, context, eActionXml, getAppPath());
	}
}

