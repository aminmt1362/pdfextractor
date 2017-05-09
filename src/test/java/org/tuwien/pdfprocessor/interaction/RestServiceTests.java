package org.tuwien.pdfprocessor.interaction;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.tuwien.pdfprocessor.processor.DocumentType;

/**
 * Created by Amin on 23.03.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RestServiceTests {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

//    @Test
    public void processPdf2Table() throws Exception {
        DocumentType doc = new DocumentType();
        doc.setType("default");
        doc.setImportToDb(Boolean.TRUE);
        doc.setSourcePath("/home/amin/Documents/amin/classification/pdf2tableresults/all/NTCIR/");

        Gson gson = new Gson();
        String json = gson.toJson(doc);
        
        this.mockMvc.perform(post("/processPdf2Table")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isAccepted());
    }
    
//    @Test
    public void processPdfgenie() throws Exception {
        DocumentType doc = new DocumentType();
        doc.setType("default");
        doc.setImportToDb(Boolean.TRUE);
        doc.setSourcePath("/home/amin/Documents/amin/classification/finalHtmlFiles_pdfgenie/all/");

        Gson gson = new Gson();
        String json = gson.toJson(doc);
        
        this.mockMvc.perform(post("/pdfgenieprocess")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isAccepted());
    }
    
//       @Test
    public void processPdfgenieGroundTruth() throws Exception {
        DocumentType doc = new DocumentType();
        doc.setType("groundtruth");
        doc.setImportToDb(Boolean.TRUE);
        doc.setSourcePath("/home/amin/Documents/amin/classification/finalHtmlFiles_pdfgenie/gt/");

        Gson gson = new Gson();
        String json = gson.toJson(doc);
        
        this.mockMvc.perform(post("/pdfgenieprocess")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isAccepted());
    }

//    @Test
    public void importGroundTruthTest() throws Exception {
        this.mockMvc.perform(post("/gtprocess")
                .contentType(contentType)
                .content(""))
                .andExpect(status().isAccepted());
    }

//    @Test
    public void importDocumentsIntoSolr() throws Exception {

        this.mockMvc.perform(post("/documentprocess")
                .contentType(contentType)
                .content("")
        ).andExpect(status().isAccepted());

        this.mockMvc.perform(post("/importtosolr")
                .contentType(contentType)
                .content("")
        ).andExpect(status().isAccepted());
    }
}
