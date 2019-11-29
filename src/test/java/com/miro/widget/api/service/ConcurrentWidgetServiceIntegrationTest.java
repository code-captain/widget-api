package com.miro.widget.api.service;

import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.WidgetDto;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Log4j2
public class ConcurrentWidgetServiceIntegrationTest {

    @Autowired
    private WidgetService service;

    @Before
    public void setUp() {
        service.deleteAll();
    }

    @Test
    public void save_WhenManyWidgetInParallelWithSingleShift_InsertionsWhereSuccessful() throws InterruptedException {
        ExecutorService e = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(4);

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto1" + Thread.currentThread().getName());
            WidgetDto widgetDto1 = new WidgetDto();
            widgetDto1.setXCoordinate(10);
            widgetDto1.setYCoordinate(30);
            widgetDto1.setWidth(100);
            widgetDto1.setHeight(50);

            service.save(widgetDto1);
            log.trace("Finish add widgetDto1" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto2" + Thread.currentThread().getName());
            WidgetDto widgetDto2 = new WidgetDto();
            widgetDto2.setXCoordinate(20);
            widgetDto2.setYCoordinate(35);
            widgetDto2.setZIndex(2L);
            widgetDto2.setWidth(100);
            widgetDto2.setHeight(50);

            service.save(widgetDto2);
            log.trace("Finish add widgetDto2" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto3" + Thread.currentThread().getName());

            WidgetDto widgetDto3 = new WidgetDto();
            widgetDto3.setXCoordinate(30);
            widgetDto3.setYCoordinate(40);
            widgetDto3.setZIndex(1L);
            widgetDto3.setWidth(100);
            widgetDto3.setHeight(50);

            service.save(widgetDto3);
            log.trace("Finish add widgetDto3" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto4" + Thread.currentThread().getName());

            WidgetDto widgetDto4 = new WidgetDto();
            widgetDto4.setXCoordinate(40);
            widgetDto4.setYCoordinate(45);
            widgetDto4.setZIndex(1L);
            widgetDto4.setWidth(100);
            widgetDto4.setHeight(50);


            service.save(widgetDto4);
            log.trace("Finish add widgetDto4" + Thread.currentThread().getName());
        });

        latch.await();
        e.shutdown();
        e.awaitTermination(5, TimeUnit.SECONDS);

        Assert.assertEquals(4, service.findAll().size());
    }

    @Test
    public void save_WhenManyWidgetInParallelWithMultipleShift_InsertionsWhereSuccessful() throws InterruptedException {
        ExecutorService e = Executors.newFixedThreadPool(6);
        CountDownLatch latch = new CountDownLatch(6);

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto1" + Thread.currentThread().getName());
            WidgetDto widgetDto1 = new WidgetDto();
            widgetDto1.setXCoordinate(10);
            widgetDto1.setYCoordinate(30);
            widgetDto1.setWidth(100);
            widgetDto1.setHeight(50);

            service.save(widgetDto1);
            log.trace("Finish add widgetDto1" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto2" + Thread.currentThread().getName());
            WidgetDto widgetDto2 = new WidgetDto();
            widgetDto2.setXCoordinate(20);
            widgetDto2.setYCoordinate(35);
            widgetDto2.setZIndex(2L);
            widgetDto2.setWidth(100);
            widgetDto2.setHeight(50);

            service.save(widgetDto2);
            log.trace("Finish add widgetDto2" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto3" + Thread.currentThread().getName());

            WidgetDto widgetDto3 = new WidgetDto();
            widgetDto3.setXCoordinate(30);
            widgetDto3.setYCoordinate(40);
            widgetDto3.setZIndex(1L);
            widgetDto3.setWidth(100);
            widgetDto3.setHeight(50);

            service.save(widgetDto3);
            log.trace("Finish add widgetDto3" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto4" + Thread.currentThread().getName());

            WidgetDto widgetDto4 = new WidgetDto();
            widgetDto4.setXCoordinate(40);
            widgetDto4.setYCoordinate(45);
            widgetDto4.setZIndex(1L);
            widgetDto4.setWidth(100);
            widgetDto4.setHeight(50);

            service.save(widgetDto4);
            log.trace("Finish add widgetDto4" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto5" + Thread.currentThread().getName());

            WidgetDto widgetDto5 = new WidgetDto();
            widgetDto5.setXCoordinate(50);
            widgetDto5.setYCoordinate(50);
            widgetDto5.setZIndex(5L);
            widgetDto5.setWidth(100);
            widgetDto5.setHeight(50);

            service.save(widgetDto5);
            log.trace("Finish add widgetDto5" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto6" + Thread.currentThread().getName());

            WidgetDto widgetDto6 = new WidgetDto();
            widgetDto6.setXCoordinate(60);
            widgetDto6.setYCoordinate(50);
            widgetDto6.setZIndex(3L);
            widgetDto6.setWidth(100);
            widgetDto6.setHeight(50);

            service.save(widgetDto6);
            log.trace("Finish add widgetDto6" + Thread.currentThread().getName());
        });

        latch.await();
        e.shutdown();
        e.awaitTermination(5, TimeUnit.SECONDS);

        Assert.assertEquals(6, service.findAll().size());
    }

    @Test
    public void save_WhenOneWidgetInParallelWithMultipleShift_InsertionsWhereSuccessful() throws InterruptedException {
        ExecutorService e = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(4);

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto1" + Thread.currentThread().getName());
            WidgetDto widgetDto1 = new WidgetDto();
            widgetDto1.setXCoordinate(10);
            widgetDto1.setYCoordinate(30);
            widgetDto1.setWidth(100);
            widgetDto1.setHeight(50);

            service.save(widgetDto1);
            log.trace("Finish add widgetDto1" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto2" + Thread.currentThread().getName());
            WidgetDto widgetDto2 = new WidgetDto();
            widgetDto2.setXCoordinate(20);
            widgetDto2.setYCoordinate(35);
            widgetDto2.setWidth(100);
            widgetDto2.setHeight(50);

            service.save(widgetDto2);
            log.trace("Finish add widgetDto2" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto3" + Thread.currentThread().getName());

            WidgetDto widgetDto3 = new WidgetDto();
            widgetDto3.setXCoordinate(30);
            widgetDto3.setYCoordinate(40);
            widgetDto3.setWidth(100);
            widgetDto3.setHeight(50);

            service.save(widgetDto3);
            log.trace("Finish add widgetDto3" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto4" + Thread.currentThread().getName());

            WidgetDto widgetDto4 = new WidgetDto();
            widgetDto4.setXCoordinate(40);
            widgetDto4.setYCoordinate(45);
            widgetDto4.setWidth(100);
            widgetDto4.setHeight(50);


            service.save(widgetDto4);
            log.trace("Finish add widgetDto4" + Thread.currentThread().getName());
        });

        latch.await();
        e.shutdown();
        e.awaitTermination(5, TimeUnit.SECONDS);

        Assert.assertEquals(4, service.findAll().size());
    }

    @Test
    public void update_WhenManyWidgetInParallelWithMultipleShift_UpdatesWhereSuccessful() throws InterruptedException {
        ExecutorService e = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(6);

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto2" + Thread.currentThread().getName());
            WidgetDto widgetDto2 = new WidgetDto();
            widgetDto2.setXCoordinate(20);
            widgetDto2.setYCoordinate(35);
            widgetDto2.setZIndex(2L);
            widgetDto2.setWidth(100);
            widgetDto2.setHeight(50);

            service.save(widgetDto2);
            log.trace("Finish add widgetDto2" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto3" + Thread.currentThread().getName());
            WidgetDto widgetDto3 = new WidgetDto();
            widgetDto3.setXCoordinate(30);
            widgetDto3.setYCoordinate(40);
            widgetDto3.setZIndex(1L);
            widgetDto3.setWidth(100);
            widgetDto3.setHeight(50);

            service.save(widgetDto3);
            log.trace("Finish add widgetDto3" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto4" + Thread.currentThread().getName());
            WidgetDto widgetDto4 = new WidgetDto();
            widgetDto4.setXCoordinate(40);
            widgetDto4.setYCoordinate(45);
            widgetDto4.setZIndex(1L);
            widgetDto4.setWidth(100);
            widgetDto4.setHeight(50);

            service.save(widgetDto4);
            log.trace("Finish add widgetDto4" + Thread.currentThread().getName());
        });

        e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto6" + Thread.currentThread().getName());
            WidgetDto widgetDto6 = new WidgetDto();
            widgetDto6.setXCoordinate(60);
            widgetDto6.setYCoordinate(60);
            widgetDto6.setZIndex(3L);
            widgetDto6.setWidth(100);
            widgetDto6.setHeight(50);

            service.save(widgetDto6);
            log.trace("Finish add widgetDto6" + Thread.currentThread().getName());
        });

        Future<WidgetDto> widget1 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto1" + Thread.currentThread().getName());
            WidgetDto widgetDto1 = new WidgetDto();
            widgetDto1.setXCoordinate(10);
            widgetDto1.setYCoordinate(30);
            widgetDto1.setWidth(100);
            widgetDto1.setHeight(50);

            log.trace("Finish add widgetDto1" + Thread.currentThread().getName());
            return service.save(widgetDto1);
        });

        Future<WidgetDto> widget5 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto5" + Thread.currentThread().getName());
            WidgetDto widgetDto5 = new WidgetDto();
            widgetDto5.setXCoordinate(50);
            widgetDto5.setYCoordinate(50);
            widgetDto5.setZIndex(5L);
            widgetDto5.setWidth(100);
            widgetDto5.setHeight(50);

            log.trace("Finish add widgetDto5" + Thread.currentThread().getName());
            return service.save(widgetDto5);
        });

        e.submit(() -> {
            log.trace("Start update widget1 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widgetDto = widget1.get();
                widgetDto.setZIndex(1L);
                widgetDto.setYCoordinate(1000);
                service.update(widgetDto.getId(), widgetDto);
                log.trace("Finish update widget1 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        e.submit(() -> {
            log.trace("Start update widget5 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widgetDto = widget5.get();
                widgetDto.setZIndex(6L);
                widgetDto.setYCoordinate(5000);
                service.update(widgetDto.getId(), widgetDto);
                log.trace("Finish update widget5 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        latch.await();
        e.shutdown();
        e.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertEquals(6, service.findAll().size());
    }

    @Test
    public void delete_WhenManyWidgetInParallel_DeletionsWhereSuccessful() throws InterruptedException {
        ExecutorService e = Executors.newFixedThreadPool(12);
        CountDownLatch latch = new CountDownLatch(6);

        Future<WidgetDto> widget1 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto1" + Thread.currentThread().getName());
            WidgetDto widgetDto1 = new WidgetDto();
            widgetDto1.setXCoordinate(10);
            widgetDto1.setYCoordinate(30);
            widgetDto1.setWidth(100);
            widgetDto1.setHeight(50);

            log.trace("Finish add widgetDto1" + Thread.currentThread().getName());
            return service.save(widgetDto1);
        });

        Future<WidgetDto> widget2 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto2" + Thread.currentThread().getName());
            WidgetDto widgetDto2 = new WidgetDto();
            widgetDto2.setXCoordinate(20);
            widgetDto2.setYCoordinate(35);
            widgetDto2.setZIndex(2L);
            widgetDto2.setWidth(100);
            widgetDto2.setHeight(50);

            log.trace("Finish add widgetDto2" + Thread.currentThread().getName());
            return service.save(widgetDto2);
        });

        Future<WidgetDto> widget3 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto3" + Thread.currentThread().getName());
            WidgetDto widgetDto3 = new WidgetDto();
            widgetDto3.setXCoordinate(30);
            widgetDto3.setYCoordinate(40);
            widgetDto3.setZIndex(1L);
            widgetDto3.setWidth(100);
            widgetDto3.setHeight(50);

            log.trace("Finish add widgetDto3" + Thread.currentThread().getName());
            return service.save(widgetDto3);
        });

        Future<WidgetDto> widget4 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto4" + Thread.currentThread().getName());
            WidgetDto widgetDto4 = new WidgetDto();
            widgetDto4.setXCoordinate(40);
            widgetDto4.setYCoordinate(45);
            widgetDto4.setZIndex(1L);
            widgetDto4.setWidth(100);
            widgetDto4.setHeight(50);

            log.trace("Finish add widgetDto4" + Thread.currentThread().getName());
            return service.save(widgetDto4);
        });

        Future<WidgetDto> widget5 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto5" + Thread.currentThread().getName());
            WidgetDto widgetDto5 = new WidgetDto();
            widgetDto5.setXCoordinate(50);
            widgetDto5.setYCoordinate(50);
            widgetDto5.setZIndex(5L);
            widgetDto5.setWidth(100);
            widgetDto5.setHeight(50);

            log.trace("Finish add widgetDto5" + Thread.currentThread().getName());
            return service.save(widgetDto5);
        });

        Future<WidgetDto> widget6 = e.submit(() -> {
            latch.countDown();
            log.trace("Start add widgetDto6" + Thread.currentThread().getName());
            WidgetDto widgetDto6 = new WidgetDto();
            widgetDto6.setXCoordinate(60);
            widgetDto6.setYCoordinate(60);
            widgetDto6.setZIndex(3L);
            widgetDto6.setWidth(100);
            widgetDto6.setHeight(50);

            log.trace("Finish add widgetDto6" + Thread.currentThread().getName());
            return service.save(widgetDto6);
        });

        e.submit(() -> {
            log.trace("Start remove widget1 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widget = widget1.get();
                service.delete(widget.getId());
                log.trace("Finish remove widget1 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        e.submit(() -> {
            log.trace("Start remove widget2 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widget = widget2.get();
                service.delete(widget.getId());
                log.trace("Finish remove widget2 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        e.submit(() -> {
            log.trace("Start remove widget3 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widget = widget3.get();
                service.delete(widget.getId());
                log.trace("Finish remove widget3 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        e.submit(() -> {
            log.trace("Start remove widget4 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widget = widget4.get();
                service.delete(widget.getId());
                log.trace("Finish remove widget4 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        e.submit(() -> {
            log.trace("Start remove widget5 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widget = widget5.get();
                service.delete(widget.getId());
                log.trace("Finish remove widget4 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        e.submit(() -> {
            log.trace("Start remove widget6 after save" + Thread.currentThread().getName());
            try {
                WidgetDto widget = widget6.get();
                service.delete(widget.getId());
                log.trace("Finish remove widget4 after save" + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });

        latch.await();
        e.shutdown();
        e.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertEquals(0, service.findAll().size());
    }

    @Test
    public void delete_WhenDeletedWidgetIsNotExistInParallel_ThrowNoSuchElementException() throws InterruptedException {
        ExecutorService e = Executors.newFixedThreadPool(1);

        e.submit(() -> {
            service.delete(UUID.randomUUID());
        });

        e.shutdown();
        e.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertEquals(0, service.findAll().size());
    }
}