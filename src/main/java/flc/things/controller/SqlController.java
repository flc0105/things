package flc.things.controller;

import flc.things.mapper.SqlMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sql")
public class SqlController {

    @Autowired
    private SqlMapper sqlMapper;

    @ApiOperation("查询")
    @PostMapping("/select")
    public ResponseEntity<?> select(@RequestParam String sql) {
        try {
            return ResponseEntity.ok(sqlMapper.executeQuery(sql));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @ApiOperation("更新")
    @PostMapping("/update")
    public ResponseEntity<?> execute(@RequestParam String sql) {
        try {
            return ResponseEntity.ok(sqlMapper.executeUpdate(sql));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
}