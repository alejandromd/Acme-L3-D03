
package acme.features.any.course;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Course;
import acme.entities.Lecture;
import acme.framework.components.accounts.Any;
import acme.framework.components.models.Tuple;
import acme.framework.services.AbstractService;

@Service
public class AnyCourseShowService extends AbstractService<Any, Course> {

	@Autowired
	protected AnyCourseRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void check() {
		boolean status;
		status = super.getRequest().hasData("id", int.class);
		super.getResponse().setChecked(status);
	}

	@Override
	public void authorise() {
		Course object;
		boolean status;
		status = super.getRequest().getPrincipal().isAuthenticated();
		final int id = super.getRequest().getData("id", int.class);
		object = this.repository.findCourseById(id);
		super.getResponse().setAuthorised(!object.isDraftMode() && status);
	}

	@Override
	public void load() {
		Course object;
		final int id = super.getRequest().getData("id", int.class);
		object = this.repository.findCourseById(id);
		super.getBuffer().setData(object);
	}

	@Override
	public void unbind(final Course object) {
		assert object != null;
		final Tuple tuple = super.unbind(object, "code", "title", "summary", "retailPrice", "link");
		final List<Lecture> lectures = this.repository.findLecturesByCourse(object.getId()).stream().collect(Collectors.toList());
		tuple.put("almaMater", object.getLecturer().getAlmaMater());
		tuple.put("courseType", object.courseTypeNature(lectures));

		super.getResponse().setData(tuple);
	}
}