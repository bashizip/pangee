package com.grew.formserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import org.camunda.bpm.engine.task.Task;
import com.grew.control.SchemasManager;
import com.grew.model.JsonSchemaDoc;
import com.grew.process.ProcessEngineManager;
import com.grew.utils.ExecutionUtils;

/**
 *
 * @author bashizip
 */
@Stateless
public class FormSchemaServer {

    @EJB
    ProcessEngineManager processEngineManager;

    @EJB
    SchemaCompiler schemaCompiler;

    public JsonSchemaDoc getJsonSchemaByFormKey(String formKey) {

        JsonSchemaDoc doc = new SchemasManager().findByFormKey(formKey);
        return doc;
    }

    public boolean validatateJson(String jsonInput, Task t) throws ProcessingException, IOException {

        t = processEngineManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery().taskId(t.getId()).initializeFormKeys().singleResult();

        // get the schema map
        Map map = getJsonSchemaByFormKey(t.getFormKey()).getSchema();

        //convert  map to json string
        ObjectMapper mapper = new ObjectMapper();
        String inputSchema = mapper.writeValueAsString(map);

        //convert the input data to json
        JsonNode data = mapper.readTree(jsonInput);

        // construct the json shema object
        JsonSchema schema = getJsonSchemaFromStringContent(inputSchema);

        //validate the data
        Set<ValidationMessage> errors = schema.validate(data);

        return errors.isEmpty();
    }

    protected JsonSchema getJsonSchemaFromStringContent(String schemaContent) {
        JsonSchemaFactory factory = new JsonSchemaFactory();
        JsonSchema schema = factory.getSchema(schemaContent);
        return schema;
    }

}
