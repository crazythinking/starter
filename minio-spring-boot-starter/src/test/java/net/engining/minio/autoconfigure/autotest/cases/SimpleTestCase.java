package net.engining.minio.autoconfigure.autotest.cases;

import cn.hutool.core.io.FileUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.engining.minio.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.storage.minio.operators.ObjectOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import java.io.InputStream;
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:27
 * @since :
 **/
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SimpleTestCase extends AbstractTestCaseTemplate {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ObjectOperations objectOperations;

    private AmazonS3 amazonS3;

    @Override
    public void initTestData() throws Exception {
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials("minio", "minio123"))
                )
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://ubuntu.wsl:9000", ""))
                .withClientConfiguration(
                        new ClientConfiguration().withMaxConnections(3).withMaxErrorRetry(1)
                )
                .build();

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        Multimap<String, String> metaData = ArrayListMultimap.create();
        metaData.put("doc", "时间轮");
        metaData.put("doc", "Timing Wheels");
        metaData.put("type", "ppt");
        objectOperations.uploadObject(
                "D:\\360Downloads\\bustream-spring-boot-starter.pdf",
                "cp001/bustream-spring-boot-starter.pdf",
                metaData
        );

        InputStream in = FileUtil.getInputStream("D:\\360Downloads\\exception.csv");
        Map<String, String> metaData2 = Maps.newHashMap();
        metaData2.put("doc", "exception.csv");
        metaData2.put("type", "csv");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(in.available());
        metadata.setUserMetadata(metaData2);
        amazonS3.putObject(
                "my-destination-bucket",
                "exception.csv",
                in,
                metadata
        );

    }

    @Override
    public void end() throws Exception {

    }
}
