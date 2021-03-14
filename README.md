# Test instructions

## 1.Start Postgresql (docker)

```shell script
docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name quarkus -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres -p 5432:5432 postgres:13
```

## 2. Maven build the application

```shell script
mvn clean install
```

## 3. Run one of the generated classes

I recommend starting target/classes/si/benchmark/database/ParallelStream.class
> **_NOTE:_** they have multiple main methods so just run the one you want to test
> **_NOTE2:_** Postgresql is configured to drop the table everytime you run the program

# BEST ParallelStream forEachOrdered (si.benchmark.database.ParallelStream)

```shell script
17:23:56.012 [main] WARN  si.benchmark.database.ParallelStream - Min date_insert 2021-03-14 17:08:54.79731
17:23:56.037 [main] WARN  si.benchmark.database.ParallelStream - Max date_insert 2021-03-14 17:23:55.981354
```

# StreamReader (si.benchmark.database.StreamReader)

## Benchmark /wo process

```shell script
12:05:24.777 [main] WARN  si.benchmark.database.StreamReader - Min date_insert 2021-03-14 11:51:44.919499
12:05:24.804 [main] WARN  si.benchmark.database.StreamReader - Max date_insert 2021-03-14 12:05:24.750259
```

## Benchmark

```shell script
13:58:27.291 [main] WARN  si.benchmark.database.StreamReader - Min date_insert 2021-03-14 12:09:28.238561
13:58:27.328 [main] WARN  si.benchmark.database.StreamReader - Max date_insert 2021-03-14 13:58:27.098906
```

# BatchStreamReader (si.benchmark.database.BatchStreamReader)

```shell script
15:22:09.671 [main] WARN  si.benchmark.database.BatchStreamReader - Min date_insert 2021-03-14 13:59:38.318545
15:22:09.700 [main] WARN  si.benchmark.database.BatchStreamReader - Max date_insert 2021-03-14 15:21:02.075079
```

# BatchStreamReaderV2 (si.benchmark.database.BatchStreamReaderV2)

```shell script
Didn't close threads in 5 seconds
Exception in thread "main" java.lang.InternalError: Executor encountered a problem closing
	at si.benchmark.database.BatchStreamReaderv2.main(BatchStreamReaderv2.java:46)

Process finished with exit code 1


Min 2021-03-14 15:24:39.618387
Max 2021-03-14 16:45:32.849851
```