
package acme.features.authenticated.tutorial;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Tutorial;
import acme.entities.tutorialSession.TutorialSession;
import acme.framework.components.accounts.Authenticated;
import acme.framework.components.models.Tuple;
import acme.framework.services.AbstractService;

@Service
public class AuthenticatedTutorialShowService extends AbstractService<Authenticated, Tutorial> {

	@Autowired
	protected AuthenticatedTutorialRepository repository;


	@Override
	public void check() {
		boolean status;

		status = super.getRequest().hasData("id", int.class);
		super.getResponse().setChecked(status);
	}

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Tutorial object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findTutorialById(id);
		super.getBuffer().setData(object);
	}

	@Override
	public void unbind(final Tutorial object) {
		assert object != null;

		Tuple tuple;
		final Collection<TutorialSession> sessions;

		sessions = this.repository.findManySessionsByTutorialId(object.getId());
		final int estimatedTime = object.estimatedTime(sessions);

		tuple = super.unbind(object, "code", "title", "informativeAbstract", "goals");
		tuple.put("assistant", object.getAssistant().getSupervisor());
		tuple.put("estimatedTime", estimatedTime);

		super.getResponse().setData(tuple);
	}

}
