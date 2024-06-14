package com.kbank.eai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    @Qualifier("eaiJob")
    private final Job eaiJob;

    // 스케줄러 등록
    @Scheduled(cron = "0 * * * * *")
    public void runBatchJob() throws Exception {
        log.info("■ Triggering Batch Job!!!");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("sBiz", "eai")
                .addString("dBiz", "fep")
                .addString("isKafka", "N")
                .addLong("chunkSize", 50L)
                .addString("guid", generateGUID())
                .toJobParameters();
        jobLauncher.run(eaiJob, jobParameters);
    }

    private String generateGUID() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateTime = dateFormat.format(new Date());
        String engiNode = "EAIA01";
        String randomSequence = generateRandonSequence();
        return dateTime + engiNode + randomSequence;
    }

    private String generateRandonSequence() {
        String characters = "1234567890abcdefg";
        Random random = new Random();

        StringBuilder randomSequence = new StringBuilder();

        for(int i=0; i<6; i++) {
            randomSequence.append(characters.charAt(random.nextInt(characters.length())));
        }

        return randomSequence.toString();
    }
}
