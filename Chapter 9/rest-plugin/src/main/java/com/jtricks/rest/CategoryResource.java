package com.jtricks.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

@Path("/category")
@Named
public class CategoryResource {

	private final ProjectManager projectManager;

	@Inject
	public CategoryResource(@ComponentImport ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@GET
	@AnonymousAllowed
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getCategories(@QueryParam("dummyParam") String dummyParam) throws SearchException {
		System.out.println(
				"This is just a dummyParam to show how parameters can be passed to REST methods:" + dummyParam);

		Collection<ProjectCategory> categories = this.projectManager.getAllProjectCategories();

		List<Category> categoryList = new ArrayList<Category>();
		for (ProjectCategory category : categories) {
			categoryList.add(new Category(category.getId(), category.getName()));
		}

		Response.ResponseBuilder responseBuilder = Response.ok(new Categories(categoryList));
		return responseBuilder.build();
	}

	@GET
	@AnonymousAllowed
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/{id}")
	public Response getCategoryFromId(@PathParam("id") String id) throws SearchException {
		ProjectCategory category = this.projectManager.getProjectCategoryObject(new Long(id));

		Response.ResponseBuilder responseBuilder = Response.ok(new Category(category.getId(), category.getName()));
		return responseBuilder.build();
	}

	@XmlRootElement
	public static class Category {
		@XmlElement
		private Long id;
		@XmlElement
		private String name;

		public Category() {
		}

		public Category(Long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	@XmlRootElement
	public static class Categories {
		@XmlElement
		private List<Category> categories;

		public Categories() {
		}

		public Categories(List<Category> categories) {
			this.categories = categories;
		}
	}

}
