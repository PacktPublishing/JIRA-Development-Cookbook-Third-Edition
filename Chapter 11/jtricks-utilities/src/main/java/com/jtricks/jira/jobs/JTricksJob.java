package com.jtricks.jira.jobs;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;

@ExportAsService({ JTricksJob.class })
@Named("jtricksJob")
public class JTricksJob implements JobRunner, InitializingBean, DisposableBean {

	private static final long EVERY_MINUTE = TimeUnit.MINUTES.toMillis(1);
	private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of(JTricksJob.class.getName());
	private static final JobId JOB_ID = JobId.of(JTricksJob.class.getName());

	private final SchedulerService scheduler;

	@Inject
	public JTricksJob(@ComponentImport SchedulerService scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public JobRunnerResponse runJob(JobRunnerRequest request) {
		System.out.println("Running JTricksJob at " + request.getStartTime());
		return JobRunnerResponse.success();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("Starting...");
		scheduler.registerJobRunner(JOB_RUNNER_KEY, this);

		final JobConfig jobConfig = JobConfig.forJobRunnerKey(JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY)
				.withSchedule(Schedule.forInterval(EVERY_MINUTE, null));

		try {
			scheduler.scheduleJob(JOB_ID, jobConfig);
		} catch (SchedulerServiceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("Stopping...");
		scheduler.unscheduleJob(JOB_ID);
	}

}
