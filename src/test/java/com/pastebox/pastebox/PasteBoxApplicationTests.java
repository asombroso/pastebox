package com.pastebox.pastebox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PasteBoxApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("When calling /main we expect to get a 200 OK status.")
	void loadingOfMainPage() throws Exception {
		mockMvc.perform(get("/main"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("When calling /login post method we expect to be redirected to the main page" +
			"if the user is present in a DB and the password is correct.")
	public void logInWithExistentUserAndCorrectPassword() throws Exception {
		mockMvc.perform(formLogin()
						.user("example1@example.com").password("Example123!"))
				.andExpect(redirectedUrl("/main"))
				.andExpect(authenticated());
	}

	@Test
	@DisplayName("When calling /login post method we expect to be redirected to the fail page" +
			"if the user isn't present in a DB or the password is incorrect.")
	public void logInWithNonExistentUser() throws Exception {
		mockMvc.perform(formLogin()
						.user("example2@example.com").password("Example123!"))
				.andExpect(redirectedUrl("/fail"))
				.andExpect(unauthenticated());
	}

	@Test
	@DisplayName("When calling /login post method we expect to be redirected to the fail page" +
			"if the user is having MFA enabled and the code hasn't been provided")
	public void logInWithExistentUserCorrectPasswordAndWithoutMfaCodeProvided() throws Exception {
		mockMvc.perform(formLogin()
						.user("example3@example.com").password("Example123!"))
				.andExpect(redirectedUrl("/fail"))
				.andExpect(unauthenticated());
	}

	@Test
	@WithAnonymousUser
	@DisplayName("When posting a private paste with an anonymous user we expect to get a 401 status.")
	public void postPrivatePasteWithAnonymousUser() throws Exception {
		mockMvc.perform(post("/main").param("data", "Example")
						.param("status", "PRIVATE"))
				.andExpect(status().is(401));
	}

	@Test
	@WithAnonymousUser
	@DisplayName("When posting a public paste with an anonymous user we expect to get a 200 OK status.")
	public void postPublicPasteWithAnonymousUser() throws Exception {
		mockMvc.perform(post("/main").param("data", "Example")
						.param("status", "PUBLIC"))
				.andExpect(status().isOk());
	}
}