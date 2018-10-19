package com.kerby.example.api.controller;

import com.kerby.example.api.mapping.CurrencyDtoMapper;
import com.kerby.example.api.models.dtos.CurrencyDto;
import com.kerby.example.api.models.dtos.ProductDto;
import com.kerby.example.api.models.responses.CurrenciesResponse;
import com.kerby.example.currency.models.CurrencyCode;
import com.kerby.example.currency.service.CurrencyService;
import com.kerby.example.database.models.PackageEntity;
import com.kerby.example.database.repositories.PackageRepository;
import com.kerby.example.packages.models.Currency;
import com.kerby.example.packages.models.Package;
import com.kerby.example.packages.models.Product;
import com.kerby.example.packages.service.PackageService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PackagesController.class)
public class PackageControllerIntegrationTest {

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private PackageRepository packageRepository;

    @MockBean
    private PackageService packageService;

    @Autowired
    private PackagesController packagesController;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void beforeTest() {
    }

    @Test
    public void when_callGetCurrencies_expect200AndCurrencyResponse() throws Exception {
        Mockito.when(packageService.getCurrencies()).thenReturn(Arrays.asList(new Currency("GBP", "British Pound Sterling")));

        this.mockMvc.perform(get("/packages-api/currency"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencies[0].code", Matchers.is("GBP")))
                .andExpect(jsonPath("$.currencies[0].name", Matchers.is("British Pound Sterling")));
    }

    @Test
    public void when_callGetPackage_expect200AndPackageResponse() throws Exception {
        final Package aPackage = new Package("Foo", "Foo test", Arrays.asList(new Product("alpha_1", "Alpha", new BigDecimal(10.00))));
        aPackage.setId(1);
        aPackage.setPrice(new BigDecimal(10));
        Mockito.when(packageService.getPackage(null, 1)).thenReturn(aPackage);

        this.mockMvc.perform(get("/packages-api/package/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.package.id", Matchers.is(1)))
                .andExpect(jsonPath("$.package.name", Matchers.is("Foo")))
                .andExpect(jsonPath("$.package.description", Matchers.is("Foo test")))
                .andExpect(jsonPath("$.package.price", Matchers.is(10)))
                .andExpect(jsonPath("$.package.products[0].id", Matchers.is("alpha_1")))
                .andExpect(jsonPath("$.package.products[0].name", Matchers.is("Alpha")))
                .andExpect(jsonPath("$.package.products[0].usdPrice", Matchers.is(10)));
    }

    @Test
    public void when_callGetPackages_expect200AndPackagesResponse() throws Exception {
        final Package aPackageFoo = new Package("Foo", "Foo test", Arrays.asList(new Product("alpha_1", "Alpha", new BigDecimal(10.00))));
        final Package aPackageBar = new Package("Bar", "Bar test", Arrays.asList(new Product("beta_1", "Beta", new BigDecimal(20.00))));
        aPackageFoo.setId(1);
        aPackageFoo.setPrice(new BigDecimal(10));
        aPackageBar.setId(1);
        aPackageBar.setPrice(new BigDecimal(20));

        Mockito.when(packageService.getPackages(null)).thenReturn(Arrays.asList(aPackageFoo, aPackageBar));

        this.mockMvc.perform(get("/packages-api/package"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$.packages[0].name", Matchers.is("Foo")))
                .andExpect(jsonPath("$.packages[0].description", Matchers.is("Foo test")))
                .andExpect(jsonPath("$.packages[0].price", Matchers.is(10)))
                .andExpect(jsonPath("$.packages[0].products[0].id", Matchers.is("alpha_1")))
                .andExpect(jsonPath("$.packages[0].products[0].name", Matchers.is("Alpha")))
                .andExpect(jsonPath("$.packages[0].products[0].usdPrice", Matchers.is(10)))
                .andExpect(jsonPath("$.packages[1].id", Matchers.is(1)))
                .andExpect(jsonPath("$.packages[1].name", Matchers.is("Bar")))
                .andExpect(jsonPath("$.packages[1].description", Matchers.is("Bar test")))
                .andExpect(jsonPath("$.packages[1].price", Matchers.is(20)))
                .andExpect(jsonPath("$.packages[1].products[0].id", Matchers.is("beta_1")))
                .andExpect(jsonPath("$.packages[1].products[0].name", Matchers.is("Beta")))
                .andExpect(jsonPath("$.packages[1].products[0].usdPrice", Matchers.is(20)));
    }

    @Test
    public void when_callCreatePackage_expect201AndPackageIdResponse() throws Exception {
        Mockito.when(packageService.createPackage("Foo", "Foo test",
                new Product("alpha_1", "Alpha", new BigDecimal(10.00)))).thenReturn(1);

        final String requestJson = "{\n" +
                "  \"package\": {\n" +
                "    \"description\": \"Foo test\",\n" +
                "    \"name\": \"Foo\",\n" +
                "    \"products\": [\n" +
                "      {\n" +
                "        \"id\": \"alpha_1\",\n" +
                "        \"name\": \"Alpha\",\n" +
                "        \"usdPrice\": 10\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        this.mockMvc.perform(post("/packages-api/package")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(content().string(Matchers.containsString("1")));
    }

    @Test
    public void when_callUpdatePackage_expect200() throws Exception {
        Mockito.when(packageService.updatePackage(1, "Foo", "Foo test",
                new Product("alpha_1", "Alpha", new BigDecimal(10.00)))).thenReturn(true);

        final String requestJson = "{\n" +
                "  \"package\": {\n" +
                "    \"description\": \"Foo test\",\n" +
                "    \"name\": \"Foo\",\n" +
                "    \"products\": [\n" +
                "      {\n" +
                "        \"id\": \"alpha_1\",\n" +
                "        \"name\": \"Alpha\",\n" +
                "        \"usdPrice\": 10\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        this.mockMvc.perform(put("/packages-api/package/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void when_callDeletePackage_expect200() throws Exception {
        Mockito.when(packageService.deletePackage(1)).thenReturn(true);

        this.mockMvc.perform(delete("/packages-api/package/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // TODO tests for error conditions (illegal params, missing packages), race conditions etc

}
