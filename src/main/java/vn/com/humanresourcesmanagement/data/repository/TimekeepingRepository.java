package vn.com.humanresourcesmanagement.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.humanresourcesmanagement.data.entity.Timekeeping;

import java.util.List;

@Repository
public interface TimekeepingRepository extends JpaRepository<Timekeeping, String> {

    @Query(value = "SELECT EXISTS (SELECT username FROM timekeeping WHERE username = :username and status = :status and work_date = current_date) ", nativeQuery = true)
    Boolean existsByUsernameCurrentDate(@Param("username") String username, @Param("status") String status);

    @Query(value = "select * from timekeeping where username = :username and status = 'CHECKIN' and work_date = current_date limit 1", nativeQuery = true)
    Timekeeping getByUsernameAndStatusCheckin(@Param("username") String username);

    @Query(value = "SELECT * FROM timekeeping WHERE username = :username and work_date = current_date limit 1", nativeQuery = true)
    Timekeeping getTimekeepingCurrent(@Param("username") String username);

    @Query(value = "SELECT * FROM timekeeping where username = :username order by work_date desc", nativeQuery = true)
    List<Timekeeping> findAllOrderCurrentDate(@Param("username") String username);

    @Query(value = "SELECT * FROM timekeeping order by work_date desc", nativeQuery = true)
    List<Timekeeping> findAllOrderWorkDate();
}
