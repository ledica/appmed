package com.appmed.app.resource;

import com.appmed.app.domain.Pessoal;
import static com.appmed.app.util.ApiVersionUtil.*;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.appmed.app.domain.Usuario;
import com.appmed.app.exceptions.NotFound;
import com.appmed.app.service.PessoalService;
import com.appmed.app.service.UsuarioService;

@RestController
@RequestMapping(value = {
    REST_APP + VERSION_V1 + USUARIO
})
public class UsuarioResource implements Serializable {

    private static final long serialVersionUID = -2827532105824714138L;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PessoalService pessoalService;

    @GetMapping("/all")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(this.usuarioService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable(name = "id") String id) throws NotFound {
        Usuario usuario = this.usuarioService.findById(id);

        if (usuario == null) {
            throw new NotFound("There is no user with this id!");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> saveUsuario(@Valid @RequestBody Usuario usuario) {
        usuario = this.usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuario);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable("id") String id, @Valid @RequestBody Usuario usuario) {
        usuario.setId(id);
        usuario = this.usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuario);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteUsuario(@PathVariable String id) {
        this.usuarioService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Usuario removido");
    }

    @PostMapping(value = "/{id}/perfil/pessoal")
    public ResponseEntity<Usuario> addPerfilPessoalUsuario(@Valid @PathVariable(name = "id") String id, @Valid @RequestBody Pessoal pessoal) throws NotFound {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            throw new NotFound("Não existe usuário com este id!");
        }
        Pessoal perfilPessoal = this.pessoalService.save(pessoal);
        usuario.setPerfilPessoal(perfilPessoal);
        usuario = this.usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuario);
    }

    @GetMapping(value = "/{id}/perfil/pessoal")
    public ResponseEntity<Pessoal> addPerfilPessoalUsuario(@PathVariable(name = "id") String id) throws NotFound {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            throw new NotFound("Não existe usuario com este id!");
        }
        Pessoal perfilPessoal = usuario.getPerfilPessoal();
        if (perfilPessoal == null) {
            throw new NotFound("Não existe perfil pessoal cadastrado para este usuário!");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(perfilPessoal);
    }

    @PutMapping(value = "/{id}/perfil/pessoal")
    public ResponseEntity<Pessoal> updatePerfilPessoalUsuario(@Valid @PathVariable(name = "id") String id, @Valid @RequestBody Pessoal pessoal) throws NotFound {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            throw new NotFound("Não existe usuário com este id!");
        }

        Pessoal perfilPessoal = usuario.getPerfilPessoal();
        if (perfilPessoal == null) {
            throw new NotFound("Não existe perfil pessoal cadastrado para este usuário!");
        }

        pessoal.setId(perfilPessoal.getId());

        pessoal = this.pessoalService.save(pessoal);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pessoal);
    }

    @DeleteMapping(value = "/{id}/perfil/pessoal")
    public ResponseEntity deletePerfilPessoalUsuario(@PathVariable String id) {
        Usuario usuario = usuarioService.findById(id);
        Pessoal perfilPessoal = usuario.getPerfilPessoal();
        usuario.setPerfilPessoal(null);
        this.usuarioService.save(usuario);
        this.pessoalService.delete(perfilPessoal.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Perfil Pessoal do usuario removido");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Usuario> authenticate(@Valid @RequestBody Usuario login) throws NotFound {
        Usuario usuario = this.usuarioService.authenticate(login.getEmail(),
                login.getPassword());

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    .body(usuario);
        } else {

            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    .body(usuario);
        }

    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Usuario>> getUsuarioByNome(@PathVariable(name = "nome") String nome) throws NotFound {
        List<Usuario> usuarios = this.usuarioService.findUserByNome(nome);

        if (usuarios == null) {
            throw new NotFound("There is no user with this name!");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(usuarios);

    }

}
