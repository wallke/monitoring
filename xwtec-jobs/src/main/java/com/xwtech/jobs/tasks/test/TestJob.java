package com.xwtech.jobs.tasks.test;

import com.xwtech.jobs.service.IZabbixLogsTool;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhangq on 2017/1/16.
 */
public class TestJob implements Job {

    final static Logger logger = LoggerFactory.getLogger(TestJob.class);

    Logger logger1 = LoggerFactory.getLogger("XWTEC_ServerCMLogger");

    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    IZabbixLogsTool zabbixLogsTool;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("test----job");
//        try{
//
//            for (int i = 0; i < 2; i++) {
//            	zabbixLogsTool.getZabbixLogs();
//            }
//
//        }catch (Exception ex){
//
//        }
        for (int i = 0; i < 300; i++) {
            String a = "appcrash\u00011.0\u0001XWMobStat_Example\u0001{0}\u0001{1}\u0001{2}\u0001750*1334\u0001{3}\u0001{4}\u0001{5}\u0001{6}\u0001{7}\u00010   CoreFoundation                      0x0000000103a4ed4b __exceptionPreprocess + 171\u00031   libobjc.A.dylib                     0x00000001034b021e objc_exception_throw + 48\u00032   CoreFoundation                      0x0000000103aa7c2f -[__NSSingleObjectArrayI objectAtIndex:] + 111\u00033   XWMobStat_Example                   0x0000000100d7f1a1 -[ViewController collectionView:didSelectItemAtIndexPath:] + 1809\u00034   UIKit                               0x000000010194f6e4 -[UICollectionView _selectItemAtIndexPath:animated:scrollPosition:notifyDelegate:] + 702\u00035   UIKit                               0x000000010197a4d2 -[UICollectionView touchesEnded:withEvent:] + 649\u00036   UIKit                               0x000000010122df6b forwardTouchMethod + 348\u00037   UIKit                               0x000000010122e034 -[UIResponder touchesEnded:withEvent:] + 49\u00038   UIKit                               0x000000010122df6b forwardTouchMethod + 348\u00039   UIKit                               0x000000010122e034 -[UIResponder touchesEnded:withEvent:] + 49\u000310  UIKit                               0x0000000101548304 _UIGestureEnvironmentSortAndSendDelayedTouches + 5645\u000311  UIKit                               0x0000000101542fcb _UIGestureEnvironmentUpdate + 1472\u000312  UIKit                               0x00000001015429c3 -[UIGestureEnvironment _deliverEvent:toGestureRecognizers:usingBlock:] + 521\u000313  UIKit                               0x0000000101541ba6 -[UIGestureEnvironment _updateGesturesForEvent:window:] + 286\u000314  UIKit                               0x0000000101087c1d -[UIWindow sendEvent:] + 3989\u000315  UIKit                               0x00000001010349ab -[UIApplication sendEvent:] + 371\u000316  UIKit                               0x000000010182172d __dispatchPreprocessedEventFromEventQueue + 3248\u000317  UIKit                               0x000000010181a463 __handleEventQueue + 4879\u000318  CoreFoundation                      0x00000001039f3761 __CFRUNLOOP_IS_CALLING_OUT_TO_A_SOURCE0_PERFORM_FUNCTION__ + 17\u000319  CoreFoundation                      0x00000001039d898c __CFRunLoopDoSources0 + 556\u000320  CoreFoundation                      0x00000001039d7e76 __CFRunLoopRun + 918\u000321  CoreFoundation                      0x00000001039d7884 CFRunLoopRunSpecific + 420\u000322  GraphicsServices                    0x0000000105391a6f GSEventRunModal + 161\u000323  UIKit                               0x0000000101016c68 UIApplicationMain + 159\u000324  XWMobStat_Example                   0x0000000100d7f5cf main + 111\u000325  libdyld.dylib                       0x00000001040d468d start + 1\u000326  ???                                 0x0000000000000001 0x0 + 1";


            List<String> platform = new ArrayList<>();
            platform.add("IOS");
            platform.add("ANDROID");

            List<String> devices = new ArrayList<>();

            devices.add("iPhone Simulator");
            devices.add("iPhone 4s");
            devices.add("iPhone 5s");
            devices.add("iPhone 6s");
            devices.add("iPhone 7");

            List<String> os = new ArrayList<>();
            os.add("10.2");
            os.add("9.3");
            os.add("8.2");

            List<String> network = new ArrayList<>();
            network.add("WIFI");
            network.add("4G");
            network.add("3G");
            List<String> exceptions = new ArrayList<>();
            exceptions.add("NSRangeException");
            exceptions.add("Runtime");

            String info = MessageFormat.format(
                    a,
                    (int) (Math.random() * 4),
            platform.get((int) (Math.random() * 2)),
                    devices.get((int) (Math.random() * 5)),
                    os.get((int) (Math.random() * 3)),
                    UUID.randomUUID().toString(),
                    network.get((int) (Math.random() * 3)),
                    DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"),
                    exceptions.get((int) (Math.random() * 2)));
            logger1.info(info);
            logger.info(info);

        }

    }
}
