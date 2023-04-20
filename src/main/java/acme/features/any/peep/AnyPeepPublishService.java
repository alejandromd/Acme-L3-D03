
package acme.features.any.peep;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Peep;
import acme.framework.components.accounts.Any;
import acme.framework.components.accounts.Principal;
import acme.framework.components.models.Tuple;
import acme.framework.helpers.MomentHelper;
import acme.framework.services.AbstractService;
import filter.SpamFilter;

@Service
public class AnyPeepPublishService extends AbstractService<Any, Peep> {

	@Autowired
	protected AnyPeepRepository repository;


	@Override
	public void check() {
		super.getResponse().setChecked(true);
	}

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Peep object;
		String nick;

		object = new Peep();

		final Date moment = MomentHelper.getCurrentMoment();
		object.setInstantiationMoment(moment);

		final Principal principal = super.getRequest().getPrincipal();

		if (principal.isAuthenticated()) {
			nick = principal.getUsername();
			object.setNick(nick);
		}

		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final Peep object) {
		assert object != null;

		super.bind(object, "instantiationMoment", "title", "nick", "message", "email", "link");
	}

	@Override
	public void validate(final Peep object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("title"))
			super.state(!SpamFilter.antiSpamFilter(object.getTitle(), this.repository.findThreshold()), "title", "any.peep.error.spam");
		if (!super.getBuffer().getErrors().hasErrors("nick"))
			super.state(!SpamFilter.antiSpamFilter(object.getNick(), this.repository.findThreshold()), "nick", "any.peep.error.spam");
		if (!super.getBuffer().getErrors().hasErrors("message"))
			super.state(!SpamFilter.antiSpamFilter(object.getMessage(), this.repository.findThreshold()), "message", "any.peep.error.spam");
	}

	@Override
	public void perform(final Peep object) {
		assert object != null;
		object.setInstantiationMoment(Date.from(Instant.now()));
		this.repository.save(object);
	}

	@Override
	public void unbind(final Peep object) {
		assert object != null;

		Tuple tuple;

		tuple = super.unbind(object, "instantiationMoment", "title", "nick", "message", "email", "link");

		super.getResponse().setData(tuple);

	}

}
