package br.com.alura.orgs.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDataBase
import br.com.alura.orgs.databinding.ActivityDetalhesProdutoBinding
import br.com.alura.orgs.extensions.formataParaMoedaBrasileira
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Produto

private const val TAG = "DetalhesProduto"
private var produto: Produto? = null

class DetalhesProdutoActivity : AppCompatActivity() {

    private var idProduto: Long? = null
    private val binding by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }


    private val produtoDao by lazy {
        AppDataBase.instancia(this).produtoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        tentaCarregarProduto()
    }

    override fun onResume() {
        super.onResume()
        idProduto?.let { id ->
            produto = produtoDao.buscaPorId(id)
        }
        produto?.let {
            preencheCampos(it)
        } ?: finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhes_produto, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_detalhes_produto_remover -> {
                produto?.let { produtoDao.remove(it) }
                finish()
            }
            R.id.menu_detalhes_produto_editar -> {
                Intent(this, FormularioProdutoActivity::class.java).apply {
                    putExtra(CHAVE_PRODUTO, produto)
                    startActivity(this)
                }
            }


        }
    return super.onOptionsItemSelected(item)
}

private fun tentaCarregarProduto() {
    intent.getParcelableExtra<Produto>(CHAVE_PRODUTO)?.let { produtoCarregado ->
        idProduto = produtoCarregado.id
        preencheCampos(produtoCarregado)
    } ?: finish()
}

private fun preencheCampos(produtoCarregado: Produto) {
    with(binding) {
        activityDetalhesProdutoImagem.tentaCarregarImagem(produtoCarregado.imagem)
        activityDetalhesProdutoNome.text = produtoCarregado.nome
        activityDetalhesProdutoDescricao.text = produtoCarregado.descricao
        activityDetalhesProdutoValor.text =
            produtoCarregado.valor.formataParaMoedaBrasileira()
    }
}

}