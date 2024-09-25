package ScreeningHumanity.TradeServer.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import ScreeningHumanity.TradeServer.global.config.ModelMapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(RestDocumentationExtension.class)
@Import({ModelMapperConfig.class})
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected DecodingToken decodingToken = Mockito.mock(DecodingToken.class);

    protected ModelMapper modelMapper;

    @BeforeEach
    void setup(RestDocumentationContextProvider provider) {
        modelMapper = new ModelMapperConfig().modelMapper();

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController(modelMapper))
                .apply(documentationConfiguration(provider).operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    protected abstract Object initController(ModelMapper modelMapper);
}
