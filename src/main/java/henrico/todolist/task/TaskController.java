package henrico.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity createTask(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        // 10/11/2023 - Current
        // 10/10/2023 - startAt
        var currentDate = LocalDateTime.now();
        var initialDate = taskModel.getStartAt();
        var finalDate = taskModel.getEndAt();
        if (currentDate.isAfter(initialDate))
            return ResponseEntity.status(400)
            .body("A data de início deve ser maior do que a data atual");
        else if(initialDate.isAfter(finalDate))
            return ResponseEntity.status(400)
            .body("A data final tem que ser maior que a data de início da tarefa");

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(200).body(task);
    }

    @GetMapping("/all")
    public ResponseEntity listAllTasks(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return ResponseEntity.status(200).body(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@RequestBody TaskModel taskModel, 
    @PathVariable UUID id, HttpServletRequest request) {

        // Pega o idUser para fazer a relacao de task com user pelo filtro, onde essa variavel foi setada de acordo com o username e o password
        var idUser = request.getAttribute("idUser");

        // Verifica se a task existe pelo id da task recebido no path da url
        var task = this.taskRepository.findById(id);
        if(task == null)
            return ResponseEntity.status(401).body("Task nao encontrada");

        var createdAt = task.getCreatedAt();

        taskModel.setCreatedAt(createdAt);
        taskModel.setIdUser((UUID) idUser);
        taskModel.setId(id);

        TaskModel updatedTask = this.taskRepository.save(taskModel);
        return ResponseEntity.status(200).body(updatedTask);
    }
}
