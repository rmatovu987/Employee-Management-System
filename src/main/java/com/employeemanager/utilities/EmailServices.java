package com.employeemanager.utilities;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;

@ApplicationScoped
public class EmailServices {

	private static Logger logger = LoggerFactory.getLogger(EmailServices.class.getName());

	@Inject
	Template signup, passwordreset, createuser;

	@Inject
	ReactiveMailer reactiveMailer;

	public Boolean signup(String fullname,  String password, String email) {

		String mail = signup.data("password", password).data("fullname", fullname).data("email", email)
				.data("systemName", Constants.systemName).data("loginUrl", Constants.loginUrl)
				.render();

		reactiveMailer.send(Mail.withHtml(email, Constants.systemName + " Account Created", mail))
				.subscribeAsCompletionStage().thenRun(() -> {
					logger.info("Email sent successfully!");
				});

		return true;

	}

	public Boolean createuser(String fullname, String email) {

		String mail = createuser.data("companyName", Constants.companyName).data("fullname", fullname)
				.data("systemName", Constants.systemName).render();

		reactiveMailer.send(Mail.withHtml(email, Constants.systemName + " Account Created", mail))
				.subscribeAsCompletionStage().thenRun(() -> {
					logger.info("Email sent successfully!");
				});

		return true;

	}

	public Boolean passwordreset(String email, String username, String password) {

		String mail = passwordreset.data("username", username).data("password", password).data("companyName", Constants.companyName)
				.data("systemName", Constants.systemName).render();

		reactiveMailer.send(Mail.withHtml(email, Constants.systemName + " Password Reset", mail))
				.subscribeAsCompletionStage().thenRun(() -> {
					logger.info("Email sent successfully!");
				});

		return true;
	}

}
