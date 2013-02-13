package com.example.model;

import com.framework.annotations.DBField;
import com.framework.annotations.DBPrimaryKey;
import com.framework.annotations.DBRelation;
import com.framework.annotations.DBTable;
import com.framework.orm.DBBaseModel;


/**
 *
 * @author schvarcz
 */
@DBTable(TableName = "Exemplo")
public class ExemploTabela extends DBBaseModel<ExemploTabela>
{
    @DBPrimaryKey
    @DBField
    private int idExemploTabela;
    //<editor-fold desc="Campos da tabela">
    @DBField
    private String nomeQualquer;
    
    @DBField
    private int idTipoTabela;
    
    @DBRelation(relation = DBRelation.DBRelations.BELONGS_TO)
    private TipoTabela tipoTabela = null;
    //</editor-fold>

    public int getIdExemploTabela()
    {
        return idExemploTabela;
    }

    public String getNomeQualquer()
    {
        return nomeQualquer;
    }

    public int getIdTipoTabela()
    {
        return idTipoTabela;
    }

    public TipoTabela getTipoTabela()
    {
        if (tipoTabela == null)
        {
            tipoTabela = new TipoTabela();
            tipoTabela.findByPk(idTipoTabela);
        }
        return tipoTabela;
    }

    public void setIdExemploTabela(int idExemploTabela)
    {
        this.idExemploTabela = idExemploTabela;
    }

    public void setNomeQualquer(String nomeQualquer)
    {
        this.nomeQualquer = nomeQualquer;
    }

    public void setIdTipoTabela(int idTipoTabela)
    {
        this.idTipoTabela = idTipoTabela;
    }

    public void setTipoTabela(TipoTabela tipoTabela)
    {
        this.tipoTabela = tipoTabela;
    }
    public void load()
    {
        super.loadRelation("tipoTabela");
    }
}
